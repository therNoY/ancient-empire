package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pers.mihao.ancient_empire.common.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.end.EndStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_path.MovePathStrategy;

/**
 * 通用处理类 处理通用的命令操做
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
        ShowAnimDTO showAnimDTO = new ShowAnimDTO(site, animList.stream().map(Anim::new).collect(Collectors.toList()));
        // TODO 每个frame的间隔 需要做成配置 默认50
        showAnimDTO.setFrame(100);
        return showAnimDTO;
    }

    /**
     * 添加单位
     *
     * @param unitId
     * @param site
     * @param armyIndex
     */
    public Unit addNewUnit(Integer unitId, Site site, Integer armyIndex) {
        Unit unit = UnitFactory.createUnit(unitId, site);
        addNewUnit(unit, armyIndex);
        return unit;
    }

    protected Unit addNewUnitAtCurrArmy(Integer unitId, Site site) {
        return addNewUnit(unitId, site, record().getCurrArmyIndex());
    }

    protected void addNewUnit(Unit unit, Integer armyIndex) {
        JSONObject addUnit = new JSONObject();
        addUnit.put(ExtMes.UNIT, unit);
        addUnit.put(ExtMes.ARMY_INDEX, armyIndex);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.ADD_UNIT, addUnit);
        // 这里深度拷贝一个新的单位防止对象数据变化
        addUnit.put(ExtMes.UNIT, BeanUtil.deptClone(unit));
    }


    /**
     * 改变单位的军队阵营
     *
     * @param armyUnitIndexByUnitId
     * @param i                     改变的军队index
     */
    protected void changeUnitArmy(Unit unit, int i) {
        showSummonAnim(unit);

        removeUnit(getArmyUnitIndexByUnitId(unit.getId()));

        JSONObject addUnit = new JSONObject();
        addUnit.put(ExtMes.UNIT, unit);
        addUnit.put(ExtMes.ARMY_INDEX, i);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.ADD_UNIT, addUnit);
    }

    /**
     * 移除单位
     *
     * @param armyUnitIndexDTO
     */
    protected void removeUnit(ArmyUnitIndexDTO armyUnitIndexDTO) {
        JSONObject removeUnit = new JSONObject();
        removeUnit.put(ExtMes.ARMY_UNIT_INDEX, armyUnitIndexDTO);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.REMOVE_UNIT, removeUnit);
    }

    /**
     * 展示攻击单位动画
     *
     * @param attach
     * @param row
     * @param column
     */
    public void showAttachAnim(Integer[] attach, ArmyUnitIndexDTO attIndex, ArmyUnitIndexDTO beAttIndex) {
        Site attSite = getUnitByIndex(attIndex);
        Site beAtt = getUnitByIndex(beAttIndex);
        // 1. 展示血量变化,
        List<LifeChangeDTO> leftChangeList = new ArrayList<>();
        leftChangeList.add(new LifeChangeDTO(attach, beAtt));

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
                floatSite = new FloatSite(attSite.getRow() + length, (double) attSite.getColumn());
            } else if (attSite.getRow() > beAtt.getRow()) {
                floatSite = new FloatSite(attSite.getRow() - length, (double) attSite.getColumn());
            } else if (attSite.getColumn() < beAtt.getColumn()) {
                floatSite = new FloatSite((double) attSite.getRow(), attSite.getColumn() + length);
            } else if (attSite.getColumn() > beAtt.getColumn()) {
                floatSite = new FloatSite((double) attSite.getRow(), attSite.getColumn() - length);
            }

            JSONObject rushUnit = new JSONObject();
            rushUnit.put(ExtMes.SITE, floatSite);
            rushUnit.put(ExtMes.ARMY_UNIT_INDEX, attIndex);

            commandStream()
                .toGameCommand().addOrderCommand(GameCommendEnum.RUSH_UNIT, rushUnit);
        }

        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, leftChangeList)
            .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_ATTACH_ANIM, showAnim);

    }

    /**
     * 展示召唤动画
     *
     * @param site
     */
    protected void showSummonAnim(Site site) {
        // 展示召唤动画
        ShowAnimDTO showAnimDTO = getShowAnim(site, gameContext.getUserTemplate().getSummonAnimation());
        JSONObject showAnim = new JSONObject();
        showAnim.put(ExtMes.ANIM, showAnimDTO);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_SUMMON_ANIM, showAnim);
    }


    /**
     * 改变当前点
     *
     * @param site
     */
    public void changeCurrPoint(Site site) {
        // 设置当前点
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_POINT, site);
    }

    /**
     * 移动单位
     *
     * @param armyUnitIndexDTO 军队和单位的index
     * @param readyMoveLine    移动路线
     * @param actions          移动结束展示action
     */
    public void moveUnit(ArmyUnitIndexDTO armyUnitIndexDTO, List<PathPosition> readyMoveLine,
        Collection<String> actions) {
        JSONObject extMes = new JSONObject();
        extMes.put(ExtMes.MOVE_LINE, readyMoveLine);
        extMes.put(ExtMes.ARMY_INDEX, armyUnitIndexDTO.getArmyIndex());
        extMes.put(ExtMes.ACTIONS, actions);
        if (CollectionUtil.isNotEmpty(actions)) {
            extMes.put(ExtMes.SITE, currSite());
        }
        commandStream().toGameCommand()
            .addOrderCommand(GameCommendEnum.MOVE_UNIT, extMes, armyUnitIndexDTO.getUnitIndex());
    }


    /**
     * 改变当前单位
     *
     * @param site
     */
    public Pair<Integer, UnitInfo> changeCurrUnit(Site site) {
        // 设置当前单位
        Pair<Integer, UnitInfo> unitInfoPair = getUnitInfoFromMapBySite(site);
        changeCurrUnit(unitInfoPair.getValue());
        return unitInfoPair;
    }

    /**
     * 改变当前单位
     *
     * @param unitInfo
     */
    public void changeCurrUnit(UnitInfo unitInfo) {
        // 设置当前单位
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, ExtMes.UNIT_INFO, unitInfo);
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
        removeUnit(armyUnitIndexDTO);

        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_UNIT_DEAD, showAnim);

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
        RegionInfo regionInfo = changeCurrRegion(region);
        regionInfo.setIndex(regionIndex);
        return regionInfo;
    }

    /**
     * 结束当前单位
     *
     * @param armyUnitIndexDTO
     */
    public void endCurrentUnit(ArmyUnitIndexDTO armyUnitIndexDTO) {
        // 处理二次移动
        List<Site> secondMoveArea = MoveAreaStrategy.getInstance()
            .getSecondMoveArea(record(), currUnit(), gameContext.getReadyMoveLine());

        if (secondMoveArea != null && secondMoveArea.size() > 0) {
            commandStream()
                .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, secondMoveArea)
                .toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ACTION);
            gameContext.setSubStatusMachine(SubStatusMachineEnum.SECOND_MOVE);
            gameContext.setStatusMachine(StatusMachineEnum.SECOND_MOVE);
        } else {
            sendEndUnitCommend(currUnit(), armyUnitIndexDTO);
        }
    }


    /**
     * 展示破坏城镇
     *
     * @param aimSite 城镇的位置
     */
    protected void showDestroyTown(Site aimSite) {
        ShowAnimDTO showDeadAnimDTO = getShowAnim(aimSite, gameContext.getUserTemplate().getDeadAnimation());
        JSONObject showDeadAnim = new JSONObject();
        showDeadAnim.put(ExtMes.ANIM, showDeadAnimDTO);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_UNIT_DEAD, showDeadAnim);

        int regionIndex = getRegionIndexBySite(aimSite);
        Region region = new Region();
        region.setType(RegionEnum.RUINS.type());
        changeRegion(regionIndex, region);
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
        return commandStream().toGameCommand()
            .addOrderCommand(GameCommendEnum.CHANG_REGION, jsonObject)
            .toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ACTION);
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
            commandStream().toGameCommand()
                .addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, endUnitDTO.getLifeChangeList());
        }

        // 处理单位血量变化
        if (endUnitDTO.getUnitStatusInfoDTOS().size() > 0) {
            changeUnitStatus(endUnitDTO.getUnitStatusInfoDTOS());
        }
        // 处理单位死亡
        UnitInfo deadUnitInfo;
        for (UnitDeadDTO deadDTO : endUnitDTO.getUnitDeadDTOList()) {
            deadUnitInfo = getUnitInfoByIndex(deadDTO);
            sendUnitDeadCommend(deadUnitInfo, deadDTO);
            gameContext.onUnitDead(deadDTO.getArmyIndex(), deadUnitInfo, this);
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
                    int restoreLife =
                        currUnit().getLevelMes().getMaxLife() - currUnit().getLife(), restore = changeLifeByDestroyTomb;
                    if (restoreLife < changeLifeByDestroyTomb) {
                        restore = restoreLife - changeLifeByDestroyTomb;
                    }
                    LifeChangeDTO lifeChangeDTO = new LifeChangeDTO(AppUtil.getArrayByInt(10, restore), currUnit());
                    commandStream().toGameCommand()
                        .addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChangeDTO);
                    unitStatusInfoDTO.setLife(unitInfo.getLife() + restore);
                } else {
                    log.info("非亡灵单位摧毁坟墓,需要减少血");
                    int desLift = changeLifeByDestroyTomb;
                    if (currUnit().getLife() < changeLifeByDestroyTomb) {
                        log.info("非亡灵单位摧毁坟墓 血量不够死亡");
                        desLift = changeLifeByDestroyTomb - currUnit().getLife();
                        unitDead = true;
                        sendUnitDeadCommend(currUnit(), currUnitArmyIndex());
                        gameContext.onUnitDead(currUnitArmyIndex().getArmyIndex(), currUnit(), this);
                    } else {
                        unitStatusInfoDTO.setLife(unitInfo.getLife() - changeLifeByDestroyTomb);
                    }
                    LifeChangeDTO lifeChangeDTO = new LifeChangeDTO(AppUtil.getArrayByInt(-1, desLift), currUnit());
                    commandStream().toGameCommand()
                        .addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChangeDTO);
                }
                break;
            }
        }

        if (!unitDead) {
            unitStatusInfoDTO.setDone(true);
            unitStatusInfoDTO.setUpdateCurr(true);
            changeUnitStatus(unitStatusInfoDTO);
            // 回调单位结束
            gameContext.onUnitDone(unitInfo, this);
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

    public void changeUnitStatus(UnitStatusInfoDTO unitStatusInfoDTOS) {
        changeUnitStatusInfo(unitStatusInfoDTOS);
    }

    public void changeUnitStatus(List<UnitStatusInfoDTO> unitStatusInfoDTOS) {
        changeUnitStatusInfo(unitStatusInfoDTOS);
    }

    private void changeUnitStatusInfo(Object obj) {
        JSONObject extData = new JSONObject(2);
        extData.put(ExtMes.UNIT_STATUS, obj);
        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_UNIT_STATUS, extData)
            .toGameCommand().addOrderCommand(GameCommendEnum.DIS_SHOW_ACTION);
    }

    public void showAction(Set<String> action) {
        JSONObject extData = new JSONObject(2);
        extData.put(ExtMes.ACTIONS, action);
        extData.put(ExtMes.SITE, currSite());
        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_ACTION, extData);
    }


    /**
     * 展示移动区域
     *
     * @param moveArea
     */
    public void showMoveArea(List<Site> moveArea) {
        gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_AREA);
        gameContext.setWillMoveArea(moveArea);
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, moveArea)
            .toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ACTION);
    }

    /**
     * 改变当前单位显示
     *
     * @param site
     */
    public Pair<Integer, UnitInfo> changeCurrentUnitShow(Site site) {
        changeCurrPoint(site);
        Pair<Integer, UnitInfo> unitMes = changeCurrUnit(site);
        changeCurrBgColor(record().getArmyList().get(unitMes.getKey()).getColor());
        changeCurrRegion(site);
        return unitMes;
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

    /**
     * 改变当前地形
     *
     * @param site
     */
    public RegionInfo changeCurrRegion(Site site) {
        // 设置当前地形
        Region region = getRegionBySite(site);
        RegionInfo regionInfo = changeCurrRegion(region);
        regionInfo.setIndex(getRegionIndexBySite(site));
        return regionInfo;
    }

    public RegionInfo changeCurrRegion(Region region) {
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, ExtMes.REGION_INFO, regionInfo);
        return regionInfo;
    }

    public void disShowAction() {
        commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ACTION);
    }


    public List<GameCommand> getCommandList() {
        return commandList;
    }


    /**
     * 展示天堂之怒动画
     */
    public void showHeavenFuryAnim(Site site) {
        Pair<Integer, Unit> unit = getUnitFromMapBySite(site);
        String heavenFuryAnim = gameContext.getHeavenFuryImg();
        String[] animArray = heavenFuryAnim.split(CommonConstant.COMMA);
        // 2. 展示攻击动画
        List<Anim> animList = new ArrayList<>();
        ShowAnimDTO showAnimDTO = new ShowAnimDTO();
        showAnimDTO.setFrame(50);
        int start = Math.max(site.getRow() - 4, 0);
        Anim anim;
        for (int i = start; i <= site.getRow(); i++) {
            for (int j = 0; j < animArray.length - 1; j++) {
                anim = new Anim(animArray[j]);
                anim.setRow(i);
                anim.setColumn(site.getColumn());
                animList.add(anim);
            }
            if (i == site.getRow()) {
                anim = new Anim(animArray[animArray.length - 1]);
                anim.setRow(i);
                anim.setColumn(site.getColumn());
                animList.add(anim);
            }
        }
        // 多一轮动画显示
        for (int j = 0; j < animArray.length; j++) {
            anim = new Anim(animArray[j]);
            anim.setRow(site.getRow());
            anim.setColumn(site.getColumn());
            animList.add(anim);
        }
        showAnimDTO.setAnimList(animList);
        JSONObject showAnim = new JSONObject();
        showAnim.put(ExtMes.ANIM, showAnimDTO);
        showAnim.put(ExtMes.ARMY_UNIT_INDEX, getArmyUnitIndexByUnitId(unit.getValue().getId()));
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.SHOW_ATTACH_ANIM, showAnim);
    }

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
    }

}
