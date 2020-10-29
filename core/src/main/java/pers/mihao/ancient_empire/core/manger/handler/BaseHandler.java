package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTransferService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.annotation.ExecuteTime;
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.ShowAnimDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.UserTemplateHelper;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础处理类 存放视图方法和基本方法 无业务操作
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\2 0002 21:25
 */
public abstract class BaseHandler extends AbstractGameEventHandler {

    private static final String CURR_ARMY_IS_NULL = "currArmyIsNull";

    private Army currArmy;

    Logger log = LoggerFactory.getLogger(this.getClass());


    protected static RegionMesService regionMesService;
    protected static UnitMesService unitMesService;
    protected static UnitLevelMesService unitLevelMesService;
    protected static UnitTransferService unitTransferService;

    static {
        regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
        unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);
        unitLevelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        unitTransferService = ApplicationContextHolder.getBean(UnitTransferService.class);
    }

    @Override
    @ExecuteTime
    public final List<Command> handler(Event event) {
        List<Command> commandList = super.handler(event);
        if (commandList != null) {
            // 根据时间处理 增删改的状态
            for (Command command : commandList) {
                GameCommand gameCommand = (GameCommand) command;
                JSONObject extMes = gameCommand.getExtMes();
                switch (gameCommand.getGameCommendEnum()) {
                    case ADD_TOMB:
                        record().getTomb().add(new Site(gameCommand.getAimSite()));
                        break;
                    case ADD_UNIT:
                        List<Unit> units = record().getArmyList().get(extMes.getInteger(ExtMes.ARMY_INDEX)).getUnits();
                        units.add((Unit) extMes.get(ExtMes.UNIT));
                        break;

                    case REMOVE_UNIT:
                        // 移除死亡的单位
                        ArmyUnitIndexDTO indexDTO = (ArmyUnitIndexDTO) extMes.get(ExtMes.ARMY_UNIT_INDEX);
                        Army army = record().getArmyList().get(indexDTO.getArmyIndex());
                        army.getUnits().remove(indexDTO.getUnitIndex().intValue());
                        break;
                    case REMOVE_TOMB:
                        // 移除坟墓
                        record().getTomb().remove(new Site(gameCommand.getAimSite()));
                        break;

                    case CHANGE_UNIT_STATUS:
                        // 处理单位升级
                        if (gameCommand.getExtMes().get(ExtMes.UNIT_STATUS) instanceof List) {
                            List<UnitStatusInfoDTO> unitStatusList = (List<UnitStatusInfoDTO>) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
                            for (UnitStatusInfoDTO unitStatus : unitStatusList) {
                                updateUnitInfo(getUnitByIndex(unitStatus), unitStatus);
                            }
                        } else {
                            UnitStatusInfoDTO unitStatus = (UnitStatusInfoDTO) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
                            updateUnitInfo(getUnitByIndex(unitStatus), unitStatus);
                        }
                        break;
                    case CHANGE_CURR_REGION:
                        record().setCurrRegion((RegionInfo) extMes.get(ExtMes.REGION_INFO));
                        break;
                    case CHANGE_CURR_UNIT:
                        record().setCurrUnit((UnitInfo) extMes.get(ExtMes.UNIT_INFO));
                        break;
                    case CHANGE_CURR_BG_COLOR:
                        gameContext.setBgColor(extMes.getString(ExtMes.BG_COLOR));
                        break;
                }
            }
        }
        return commandList;
    }

    /**
     * 处理单位升级
     *
     * @param gameCommand
     */
    @Override
    protected final void handlerLevelUp(GameCommand gameCommand) {
        if (gameCommand.getExtMes().get(ExtMes.UNIT_STATUS) instanceof List) {
            List<UnitStatusInfoDTO> unitStatusList = (List<UnitStatusInfoDTO>) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
            for (UnitStatusInfoDTO unitStatus : unitStatusList) {
                handlerLevelUp(unitStatus);
            }
        } else {
            UnitStatusInfoDTO unitStatus = (UnitStatusInfoDTO) gameCommand.getExtMes().get(ExtMes.UNIT_STATUS);
            handlerLevelUp(unitStatus);
        }

    }

    private void handlerLevelUp(UnitStatusInfoDTO unitStatus) {
        // 更新单位的状态
        Unit unit = getUnitByIndex(unitStatus);
        // 判断是否升级
        Integer levelExp = gameContext.getLevelExp(unit.getLevel());
        if (unitStatus.getExperience() != null && unitStatus.getExperience() >= levelExp) {
            // 可以升级
            int maxLevel = gameContext.getUserTemplate().getUnitMaxLevel();
            if (maxLevel == unit.getLevel()) {
                // 设置最大经验值 不升级
                unitStatus.setExperience(levelExp);
            } else {
                // 升级
                boolean isPromotion = false;
                if ((UserTemplateHelper.COMMON.equals(gameContext.getUserTemplate().getPromotionMode())
                        || UserTemplateHelper.RANDOM.equals(gameContext.getUserTemplate().getPromotionMode()))
                        && unit.getLevel() + 1 > gameContext.getUserTemplate().getPromotionLevel()) {
                    // 开启晋升模式 同一个兵种的最大晋级数量1
                    Army army = record().getArmyList().get(unitStatus.getArmyIndex());
                    int count = 0;
                    int typeCount = 0;
                    for (Unit u : army.getUnits()) {
                        if (Boolean.TRUE.equals(u.getPromotion())) {
                            if (u.getTypeId().equals(unit.getTypeId())) {
                                typeCount++;
                            } else {
                                count++;
                            }
                        }
                    }
                    // 最大晋升数量 和类型晋升数量都小于模板最大数量才能晋级
                    if (count < gameContext.getUserTemplate().getPromotionMaxNum() &&
                            typeCount < gameContext.getTypePromotionCount()) {
                        if (gameContext.getRandomPromotionChance()) {
                            log.info("准备晋升");
                            QueryWrapper<UnitTransfer> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("unitId", unit.getTypeId())
                                    .eq("order", 1);
                            UnitTransfer unitTransfer = unitTransferService.getOne(queryWrapper);
                            if (unitTransfer != null) {
                                isPromotion = true;
                                unit.setLevel(unit.getLevel() + 1);
                                unit.setExperience(unit.getExperience() - levelExp);
                                Unit newUnit = new Unit();
                                BeanUtil.copyValue(unit, newUnit);
                                newUnit.setTypeId(unitTransfer.getTransferUnitId());
                                commandStream()
                                        .toGameCommand().removeUnit(unitStatus)
                                        .toGameCommand().addUnit(newUnit, unitStatus.getArmyIndex());
                            }

                        }

                    }
                }
                if (!isPromotion) {
                    unitStatus.setLevel(unit.getLevel() + 1);
                    unitStatus.setExperience(unit.getExperience() - levelExp);
                    commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_LEVEL_UP, unit);
                }
            }
        }
    }

    /**
     * 根据颜色判断是否是同一个阵营的
     *
     * @param color
     * @return
     */
    protected boolean colorIsCamp(String color) {
        return currArmy().getCamp() == getCampByColor(color);
    }

    protected int getCampByColor(String color) {
        for (Army army : record().getArmyList()) {
            if (color.equals(army.getColor())) {
                return army.getCamp();
            }
        }
        return -1;
    }


    private void updateUnitInfo(Unit unit, Object from) {
        BeanUtil.copyValue(from, unit);
    }

    /**
     * 根据位置和动画构建展示动画DTO
     *
     * @param site
     * @param animStrings
     * @return
     */
    protected ShowAnimDTO getShowAnim(Site site, String animStrings) {
        String[] anims = animStrings.split(BaseConstant.COMMA);
        List<String> animList = Arrays.stream(anims)
                .map(s -> gameContext.getUserTemplate().getId() + BaseConstant.LINUX_SEPARATOR + s)
                .collect(Collectors.toList());
        // 这里强行改成偶数个图片
        if (animList.size() % 2 != 0) {
            animList.add(animList.get(animList.size() - 1));
        }
        ShowAnimDTO showAnimDTO = new ShowAnimDTO(site, animList);
        // TODO 每个frame的间隔 需要做成配置 默认50
        showAnimDTO.setFrame(100);
        return showAnimDTO;
    }

    /**
     * 从整个地图根据位置获取单位
     *
     * @param site
     * @return 军队index 和单位
     */
    protected Pair<Integer, Unit> getUnitFromMapBySite(Site site) {
        Army army;
        Unit unit;
        for (int i = 0; i < record().getArmyList().size(); i++) {
            army = record().getArmyList().get(i);
            unit = getUnitFromArmyBySite(army, site);
            if (unit != null) {
                return new Pair<>(i, unit);
            }
        }
        return null;
    }


    /**
     * 从当前军队中获取单位
     *
     * @param site
     * @return
     */
    protected Unit getUnitFromCurrArmyBySite(Site site) {
        return getUnitFromArmyBySite(currArmy(), site);
    }

    /**
     * 基础方法 在指定军队中 指定位置获取单位
     *
     * @param army
     * @param site
     * @return
     */
    protected Unit getUnitFromArmyBySite(Army army, Site site) {
        for (Unit unit : army.getUnits()) {
            if (AppUtil.siteEquals(unit, site)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 获取单位在地图中的index
     * @param army
     * @param uuid
     * @return
     */
    protected Integer getUnitIndex(Army army, String uuid) {
        if (army != null) {
            for (int i = 0; i < army.getUnits().size(); i++) {
                if (army.getUnits().get(i).getId().equals(uuid)) {
                    return i;
                }
            }
        } else {
            for (Army a : gameContext.getUserRecord().getArmyList()) {
                for (int i = 0; i < a.getUnits().size(); i++) {
                    if (a.getUnits().get(i).getId().equals(uuid)) {
                        return i;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 从当前军队获取当前单位
     *
     * @return
     */
    protected Unit getCurrUnitFromArmy() {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (record().getCurrUnit().getId().equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 根据军队和单位Index 获取单位
     * @param indexDTO
     * @return
     */
    protected Unit getUnitByIndex(ArmyUnitIndexDTO indexDTO) {
        return record().getArmyList().get(indexDTO.getArmyIndex()).getUnits().get(indexDTO.getUnitIndex());
    }

    /**
     * 通过index 获取单位的info
     *
     * @param indexDTO
     * @return
     */
    protected UnitInfo getUnitInfoByIndex(ArmyUnitIndexDTO indexDTO) {
        Unit unit = getUnitByIndex(indexDTO);
        UnitInfo unitInfo = unitMesService.getUnitInfo(unit.getTypeId().toString(), unit.getLevel());
        BeanUtil.copyValueFromParent(unit, unitInfo);
        return unitInfo;
    }

    /**
     * 从当前单位获取军队
     * @param unitId
     * @return
     */
    protected Unit getUnitFromCurrArmy(String unitId) {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unitId.equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 获取当前单位的index
     * @return
     */
    protected Integer getCurrUnitIndex() {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (record().getCurrUnit().getId().equals(unit.getId())) {
                return i;
            }
        }
        return null;
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected int getRegionIndexBySite(Site site) {
        return (site.getRow() - 1) * gameMap().getColumn() - 1 + site.getColumn();
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected Region getRegionBySite(Site site) {
        return gameMap().getRegions().get(getRegionIndexBySite(site));
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected Region getRegionBySite(int row, int column) {
        return gameMap().getRegions().get((row - 1) * gameMap().getColumn() - 1 + column);
    }

    protected boolean siteInArea(Site site, List<Site> sites) {
        return sites.contains(site);
    }

    /**
     * 通过地形的index获取地形位置
     *
     * @param index
     * @return
     */
    protected Site getSiteByRegionIndex(Integer index) {
        int gameColumn = gameMap().getColumn();
        int row = (index + 1) / gameColumn + 1;
        int column = (index + 1) % gameColumn;
        return new Site(row, column == 0 ? gameColumn : column);
    }

    /**
     * 获取当前单位的
     *
     * @return
     */
    protected Site getCurrentUnitSite() {
        return new Site(record().getCurrUnit().getRow(), record().getCurrUnit().getColumn());
    }

    /**
     * 获取当前单位 当前位置的 的 攻击范围
     *
     * @param unitMes
     * @param aimP
     * @param userRecord
     * @return
     */
    protected List<Site> getAttachArea() {
        UnitMes unitMes = record().getCurrUnit().getUnitMes();
        Site currentPoint = record().getCurrPoint();
        Integer maxRange = unitMes.getMaxAttachRange();
        List<Site> maxAttach = new ArrayList<>();
        int minI = Math.max(currentPoint.getRow() - maxRange, 1);
        int maxI = Math.min(currentPoint.getRow() + maxRange + 1, gameMap().getRow() + 1);
        int minJ = Math.max(currentPoint.getColumn() - maxRange, 1);
        int maxJ = Math.min(currentPoint.getColumn() + maxRange + 1, gameMap().getRow() + 1);
        for (int i = minI; i < maxI; i++) {
            for (int j = minJ; j < maxJ; j++) {
                if (getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) <= maxRange && getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) > 0) {
                    maxAttach.add(new Site(i, j));
                }
            }
        }
        Integer minRange = unitMes.getMinAttachRange();
        List<Site> notAttach = null;
        if (minRange != 1) {
            // 获取无法攻击到的点
            minRange = minRange - 1;
            notAttach = new ArrayList<>();
            minI = Math.max(currentPoint.getRow() - minRange, 0);
            maxI = Math.min(currentPoint.getRow() + minRange + 1, gameMap().getRow());
            minJ = Math.max(currentPoint.getColumn() - minRange, 0);
            maxJ = Math.min(currentPoint.getColumn() + minRange + 1, gameMap().getRow());
            for (int i = minI; i < maxI; i++) {
                for (int j = minJ; j < maxJ; j++) {
                    if (getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) <= minRange) {
                        notAttach.add(new Site(i, j));
                    }
                }
            }

        }

        int row = gameMap().getRow();
        int column = gameMap().getColumn();
        // 过滤符合条件的点
        List<Site> finalNotAttach = notAttach;
        return maxAttach.stream().filter(site -> {
            // 在地图范围内
            if (site.getRow() <= row && site.getColumn() <= column) {
                // 不在不可攻击范围内
                if (finalNotAttach == null || !finalNotAttach.contains(site)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 判断游戏的状态机是否在这些中
     *
     * @param enums
     * @return
     */
    protected boolean stateIn(StatusMachineEnum... enums) {
        for (StatusMachineEnum statusEnum : enums) {
            if (gameContext.getStatusMachine().equals(statusEnum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断游戏的状态机是否在这些中
     *
     * @param enums
     * @return
     */
    protected boolean subStateIn(SubStatusMachineEnum... enums) {
        for (SubStatusMachineEnum statusEnum : enums) {
            if (gameContext.getSubStatusMachine().equals(statusEnum)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断游戏的状态机是否不在这些中
     *
     * @param enums
     * @return
     */
    protected boolean stateNotIn(StatusMachineEnum... enums) {
        return !stateIn(enums);
    }


    /**
     * 当前记录
     *
     * @return 记录视图
     */
    protected UserRecord record() {
        return gameContext.getUserRecord();
    }

    /**
     * 当前记录
     *
     * @return 记录视图
     */
    protected GameMap gameMap() {
        return record().getGameMap();
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    protected Army currArmy() {
        if (currArmy == null) {
            for (Army army : record().getArmyList()) {
                if (army.getCamp().equals(record().getCurrCamp())) {
                    this.currArmy = army;
                    break;
                }
            }
        }
        Assert.notNull(currArmy, CURR_ARMY_IS_NULL);
        return currArmy;
    }


    /**
     * game视图
     *
     * @return 当前单位
     */
    protected UnitInfo currUnit() {
        return record().getCurrUnit();
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    protected Site currSite() {
        return record().getCurrPoint();
    }


    /**
     * 获取两点的距离
     *
     * @param row
     * @param column
     * @param row2
     * @param column2
     * @return
     */
    private int getSiteLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

}
