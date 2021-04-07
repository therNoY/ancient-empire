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
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.UserTemplateHelper;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.end.EndStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_path.MovePathStrategy;

/**
 * 通用处理类 处理通用的业务操做
 *
 * @author mihao
 * @version 1.0
 * @date 2020\10\4 0004 8:19
 */
public class CommonHandler extends AbstractGameEventHandler {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据位置和动画构建展示动画DTO
     *
     * @param site
     * @param animStrings
     * @return
     */
    public ShowAnimDTO getShowAnim(Site site, String animStrings) {
        String[] anims = animStrings.split(CommonConstant.COMMA);
        List<String> animList = Arrays.stream(anims)
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

    public void addNewUnit(Integer unitId, Site site, Integer armyIndex) {
        JSONObject addUnit = new JSONObject();
        Unit unit = UnitFactory.createUnit(unitId, site);
        addUnit.put(ExtMes.UNIT, unit);
        addUnit.put(ExtMes.ARMY_INDEX, armyIndex);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.ADD_UNIT, addUnit);
    }

    protected void addNewUnit(Integer unitId, Site site) {
        addNewUnit(unitId, site, record().getCurrArmyIndex());
    }

    /**
     * 展示攻击单位动画
     *
     * @param attach
     * @param row
     * @param column
     */
    public void showAttachAnim(Integer[] attach, Site attSite, Site beAtt, ArmyUnitIndexDTO attIndex, ArmyUnitIndexDTO beAttIndex) {

        // 1. 展示血量变化,
        List<LifeChangeDTO> leftChangeDTOS = new ArrayList<>();
        leftChangeDTOS.add(new LifeChangeDTO(attach, beAtt));

        // 2. 展示攻击动画
        ShowAnimDTO showAnimDTO = getShowAnim(beAtt, gameContext.getUserTemplate().getAttachAnimation());
        JSONObject showAnim = new JSONObject();
        showAnim.put(ExtMes.ANIM, showAnimDTO);
        showAnim.put(ExtMes.ARMY_UNIT_INDEX, beAttIndex);

        // 判断是否突袭
        if (AppUtil.isReach(attSite, beAtt)) {
            FloatSite floatSite = null;
            double length = 0.3;
            if (attSite.getRow() < beAtt.getRow()) {
                floatSite = new FloatSite(attSite.getRow() + length, (double)attSite.getColumn());
            }else if (attSite.getRow() > beAtt.getRow()) {
                floatSite = new FloatSite(attSite.getRow() - length, (double)attSite.getColumn());
            }else if (attSite.getColumn() < beAtt.getColumn()) {
                floatSite = new FloatSite((double)attSite.getRow(), attSite.getColumn()  + length);
            }else if (attSite.getColumn() > beAtt.getColumn()) {
                floatSite = new FloatSite((double)attSite.getRow(), attSite.getColumn()  - length);
            }

            JSONObject rushUnit = new JSONObject();
            rushUnit.put(ExtMes.SITE, floatSite);
            rushUnit.put(ExtMes.ARMY_UNIT_INDEX, attIndex);

            commandStream()
                .toGameCommand().addOrderCommand(GameCommendEnum.RUSH_UNIT, rushUnit);
        }

        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, leftChangeDTOS)
            .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_ATTACH_ANIM, showAnim);

    }


    /**
     * 改变当前点
     *
     * @param site
     */
    public void changeCurrPoint(Site site) {
        // 设置当前点
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_POINT, site);
        record().setCurrPoint(site);
    }


    /**
     * 改变当前单位
     *
     * @param site
     */
    public Pair<Integer, UnitInfo> changeCurrUnit(Site site) {
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
     * @param unitInfo
     */
    public void changeCurrUnit(UnitInfo unitInfo) {
        // 设置当前单位
        record().setCurrUnit(unitInfo);
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, ExtMes.UNIT_INFO, unitInfo);
    }

    /**
     * 获取当前单位的index
     *
     * @return
     */
    public ArmyUnitIndexDTO currUnitArmyIndex() {
        return new ArmyUnitIndexDTO(record().getCurrArmyIndex(), getCurrUnitIndex());
    }

    /**
     * 获取unitInfo 通过位置获取
     *
     * @param site
     */
    public Pair<Integer, UnitInfo> getUnitInfoFromMapBySite(Site site) {
        // 设置当前单位
        Pair<Integer, Unit> unitMes = getUnitFromMapBySite(site);
        if (unitMes == null) {
            // 破化者供给房子
            return null;
        }
        Integer id = unitMes.getValue().getTypeId();
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
    public RegionInfo changeCurrRegion(Site site) {
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
    public void sendUnitDeadCommend(UnitInfo unit, ArmyUnitIndexDTO armyUnitIndexDTO) {
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
    public RegionInfo changeCurrRegion(Integer regionIndex) {
        // 设置当前地形
        Region region = gameMap().getRegions().get(regionIndex);
        return changeCurrRegion(region);
    }

    /**
     * 结束当前单位
     *
     * @param armyUnitIndexDTO
     */
    public void endCurrentUnit(ArmyUnitIndexDTO armyUnitIndexDTO) {
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
    public Stream changeRegion(int regionIndex, Region region) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ExtMes.REGION_INDEX, regionIndex);
        jsonObject.put(ExtMes.REGION, region);
        record().getGameMap().getRegions().set(regionIndex, region);
        return commandStream().toGameCommand().addOrderCommand(GameCommendEnum.CHANG_REGION, jsonObject);
    }

    /**
     * 封装结束单位命令 单位踩坟墓事件处理
     *
     * @param armyUnitIndexDTO
     */
    public void sendEndUnitCommend(UnitInfo unitInfo, ArmyUnitIndexDTO armyUnitIndexDTO) {
        // 触发单位结束移动事件
        EndUnitDTO endUnitDTO = EndStrategy.getInstance().getEndUnitResult(this);
        // 处理生命值改变
        if (endUnitDTO.getLifeChangeList().size() > 0) {
            commandStream().toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, endUnitDTO.getLifeChangeList());
        }

        // 处理单位血量变化
        if (endUnitDTO.getUnitStatusInfoDTOS().size() > 0) {
            commandStream().toGameCommand().changeUnitStatus(endUnitDTO.getUnitStatusInfoDTOS());
        }
        // 处理单位死亡
        for (UnitDeadDTO deadDTO : endUnitDTO.getUnitDeadDTOList()) {
            sendUnitDeadCommend(getUnitInfoByIndex(deadDTO), deadDTO);
        }

        // 修改单位的状态有顺序（结束回合）
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);

        // 处理单位是否站在坟墓上
        int changeLifeByDestroyTomb = gameContext.getChangeLifeByDestroyTomb();
        boolean isUnDead = unitInfo.getAbilities().contains(AbilityEnum.UNDEAD.ability()), unitDead = false;
        for (Tomb tomb : record().getTombList()) {
            if (AppUtil.siteEquals(tomb, currUnit())) {
                // 摧毁坟墓消失
                commandStream().toGameCommand().addCommand(GameCommendEnum.REMOVE_TOMB, tomb);
                if (isUnDead) {
                    log.info("亡灵单位摧毁坟墓,需要加血");
                    int restoreLife = currUnit().getLevelMes().getMaxLife() - currUnit().getLife(), restore = changeLifeByDestroyTomb;
                    if (restoreLife < changeLifeByDestroyTomb) {
                        restore = restoreLife - changeLifeByDestroyTomb;
                    }
                    LifeChangeDTO lifeChangeDTO = new LifeChangeDTO(AppUtil.getArrayByInt(10, restore), currUnit());
                    commandStream().toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChangeDTO);
                    unitStatusInfoDTO.setLife(unitInfo.getLife() + restore);
                } else {
                    log.info("非亡灵单位摧毁坟墓,需要减少血");
                    int desLift = changeLifeByDestroyTomb;
                    if (currUnit().getLife() < changeLifeByDestroyTomb) {
                        log.info("非亡灵单位摧毁坟墓 血量不够死亡");
                        desLift = changeLifeByDestroyTomb - currUnit().getLife();
                        unitDead = true;
                        sendUnitDeadCommend(currUnit(), currUnitArmyIndex());
                    } else {
                        unitStatusInfoDTO.setLife(unitInfo.getLife() - changeLifeByDestroyTomb);
                    }
                    LifeChangeDTO lifeChangeDTO = new LifeChangeDTO(AppUtil.getArrayByInt(-1, desLift), currUnit());
                    commandStream().toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChangeDTO);
                }
                break;
            }
        }

        if (!unitDead) {
            unitStatusInfoDTO.setDone(true);
            unitStatusInfoDTO.setUpdateCurr(true);
            commandStream().toGameCommand().changeUnitStatus(unitStatusInfoDTO);
        }
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
    public void showMoveArea(List<Site> moveArea) {
        gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_AREA);
        gameContext.setWillMoveArea(moveArea);
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, moveArea);
    }

    /**
     * 展示移动路线
     */
    public List<PathPosition> showMoveLine(Site aimSite) {
        MovePathDTO movePathDTO = MovePathStrategy.getInstance().getUnitMovePath(record().getCurrUnit(),
                aimSite, record(), currUnit());
        List<PathPosition> path = movePathDTO.getPositionList();
        gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_LINE);
        gameContext.setReadyMoveLine(path);
        gameContext.setStartMoveSite(getCurrentUnitSite());
        gameContext.setReadyMoveSite(aimSite);
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_LINE, ExtMes.MOVE_LINE, path);
        return path;
    }

    public RegionInfo changeCurrRegion(Region region) {
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, ExtMes.REGION_INFO, regionInfo);
        return regionInfo;
    }


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

    }

}
