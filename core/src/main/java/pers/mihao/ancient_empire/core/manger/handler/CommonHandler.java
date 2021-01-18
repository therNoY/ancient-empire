package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
import pers.mihao.ancient_empire.core.dto.ShowAnimDTO;
import pers.mihao.ancient_empire.core.dto.UnitDeadDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.UserTemplateHelper;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.end.EndStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 * 通用处理类
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\4 0004 8:19
 */
public class CommonHandler extends AbstractGameEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

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

    @Override
    protected final void addCommand(Command command) {
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
                // 更新单位状态变化
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
        commandList.add(command);
    }

    protected void updateUnitInfo(Unit unit, Object from) {
        BeanUtil.copyValue(from, unit);
        unit.setRow(currSite().getRow());
        unit.setColumn(currSite().getColumn());
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
     * 改变当前点
     *
     * @param site
     */
    protected void changeCurrPoint(Site site) {
        // 设置当前点
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_POINT, site);
        record().setCurrPoint(site);
    }


    /**
     * 改变当前单位
     *
     * @param site
     */
    protected Pair<Integer, UnitInfo> changeCurrUnit(Site site) {
        // 设置当前单位
        Pair<Integer, UnitInfo> unitInfoPair = getUnitInfoFromMapBySite(site);
        if (currUnit() == null) {
            record().setCurrUnit(unitInfoPair.getValue());
        }
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, ExtMes.UNIT_INFO, unitInfoPair.getValue());
        return unitInfoPair;
    }

    /**
     * 改变当前单位
     *
     * @param site
     */
    protected void changeCurrUnit(UnitInfo unitInfo) {
        // 设置当前单位
        record().setCurrUnit(unitInfo);
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, ExtMes.UNIT_INFO, unitInfo);
    }

    /**
     * 获取当前单位的index
     *
     * @return
     */
    protected ArmyUnitIndexDTO currUnitArmyIndex() {
        return new ArmyUnitIndexDTO(record().getCurrArmyIndex(), getCurrUnitIndex());
    }

    /**
     * 获取unitInfo 通过位置获取
     *
     * @param site
     */
    protected Pair<Integer, UnitInfo> getUnitInfoFromMapBySite(Site site) {
        // 设置当前单位
        Pair<Integer, Unit> unitMes = getUnitFromMapBySite(site);
        if (unitMes == null) {
            // 破化者供给房子
            return null;
        }
        String id = unitMes.getValue().getTypeId().toString();
        UnitInfo unitInfo = unitMesService.getUnitInfo(id, unitMes.getValue().getLevel());

        BeanUtil.copyValueFromParent(unitMes.getValue(), unitInfo);
        unitInfo.setRegionInfo(getRegionInfoBySite(unitInfo.getRow(), unitInfo.getColumn()));
        return new Pair<>(unitMes.getKey(), unitInfo);
    }

    /**
     * 改变当前地形
     *
     * @param site
     */
    protected RegionInfo changeCurrRegion(Site site) {
        // 设置当前地形
        Region region = getRegionBySite(site);
        return changeCurrRegion(region);
    }

    /**
     * 发送单位死亡命令
     *
     * @param unit             死亡单位
     * @param armyUnitIndexDTO 单位的index
     */
    protected void sendUnitDeadCommend(UnitInfo unit, ArmyUnitIndexDTO armyUnitIndexDTO) {
        // 获取展示单位死亡的动画
        ShowAnimDTO showAnimDTO = getShowAnim(unit, gameContext.getUserTemplate().getDeadAnimation());
        JSONObject showAnim = new JSONObject();
        showAnim.put(ExtMes.ANIM, showAnimDTO);

        // 单位死亡, 展示单位死亡动画
        commandStream()
                .toGameCommand().removeUnit(armyUnitIndexDTO)
                .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_UNIT_DEAD, showAnim);

        // 判断是否有坟墓
        boolean isHasTomb = true;
        for (Ability ability : unit.getAbilities()) {
            if (ability.getType().equals(AbilityEnum.CASTLE_GET.type()) || ability.getType()
                    .equals(AbilityEnum.UNDEAD.type())) {
                isHasTomb = false;
                break;
            }
        }
        if (isHasTomb) {
            commandStream().toGameCommand().addOrderCommand(GameCommendEnum.ADD_TOMB, unit);
        }
    }


    /**
     * 改变当前地形
     *
     * @param regionIndex
     */
    protected RegionInfo changeCurrRegion(Integer regionIndex) {
        // 设置当前地形
        Region region = gameMap().getRegions().get(regionIndex);
        return changeCurrRegion(region);
    }



    /**
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoByRegionIndex(Integer reginxIndex) {
        return getRegionInfoBySite(AppUtil.getSiteByMapIndex(reginxIndex, gameMap().getColumn()));
    }

    /**
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoBySite(Site site) {
        return getRegionInfoBySite(site.getRow(), site.getColumn());
    }

    /**
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoBySite(int row, int column) {
        Region region = getRegionBySite(row, column);
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        return regionInfo;
    }

    /**
     * 结束当前单位
     *
     * @param armyUnitIndexDTO
     */
    protected void endCurrentUnit(ArmyUnitIndexDTO armyUnitIndexDTO) {
        // 处理二次移动
        List<Site> secondMoveArea = MoveAreaStrategy.getInstance().getSecondMoveArea(record(), currUnit(), gameContext.getReadyMoveLine());

        if (secondMoveArea != null && secondMoveArea.size() > 0) {
            commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, secondMoveArea);
            gameContext.setSubStatusMachine(SubStatusMachineEnum.SECOND_MOVE);
            gameContext.setStatusMachine(StatusMachineEnum.SECOND_MOVE);
        } else {
            sendEndUnitCommend(currUnit(), armyUnitIndexDTO);
        }
    }


    /**
     * 改变地形信息
     *
     * @param regionIndex
     * @param region
     */
    protected Stream changeRegion(int regionIndex, Region region) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ExtMes.REGION_INDEX, regionIndex);
        jsonObject.put(ExtMes.REGION, region);
        record().getGameMap().getRegions().set(regionIndex, region);
        return commandStream().toGameCommand().addOrderCommand(GameCommendEnum.CHANG_REGION, jsonObject);
    }

    /**
     * 封装结束单位命令
     *
     * @param armyUnitIndexDTO
     */
    protected void sendEndUnitCommend(UnitInfo unitInfo, ArmyUnitIndexDTO armyUnitIndexDTO) {
        // 触发单位结束移动事件
        EndUnitDTO endUnitDTO = EndStrategy.getInstance().getEndUnitResult(record());

        // 处理生命值改变
        if (endUnitDTO.getLifeChangeList().size() > 0) {
            commandStream().toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, endUnitDTO.getLifeChangeList());
        }

        // 处理单位血量变化
        if (endUnitDTO.getUnitStatusInfoDTOS().size() > 0) {
            UnitStatusInfoDTO[] unitStatusInfoDTOS = new UnitStatusInfoDTO[endUnitDTO.getUnitStatusInfoDTOS().size()];
            endUnitDTO.getUnitStatusInfoDTOS().toArray(unitStatusInfoDTOS);
            commandStream().toGameCommand().changeUnitStatus(unitStatusInfoDTOS);
        }
        // 处理单位死亡
        for (UnitDeadDTO deadDTO : endUnitDTO.getUnitDeadDTOList()) {
            sendUnitDeadCommend(getUnitInfoByIndex(deadDTO), deadDTO);
        }
        // 修改单位的状态有顺序（结束回合）
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);
        unitStatusInfoDTO.setDone(true);
        commandStream().toGameCommand().changeUnitStatus(unitStatusInfoDTO);
        gameContext.setStatusMachine(StatusMachineEnum.INIT);
    }

    /**
     * 改变当前颜色
     *
     * @param color
     */
    public void changeCurrBgColor(String color) {
        // 设置前端执行指令
        commandStream()
                .toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_BG_COLOR, ExtMes.BG_COLOR, color);
    }


    /**
     * 展示移动区域
     *
     * @param moveArea
     */
    protected void showMoveArea(List<Site> moveArea) {
        gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_AREA);
        gameContext.setWillMoveArea(moveArea);
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, moveArea);
    }

    private RegionInfo changeCurrRegion(Region region) {
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, ExtMes.REGION_INFO, regionInfo);
        return regionInfo;
    }


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

    }

}
