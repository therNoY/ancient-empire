package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.end.EndStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * 通用处理类
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\4 0004 8:19
 */
public class CommonHandler extends BaseHandler {

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
     * @param site
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

    private RegionInfo changeCurrRegion(Region region) {
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, ExtMes.REGION_INFO, regionInfo);
        return regionInfo;
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
        updateOrderUnitInfo(armyUnitIndexDTO).setDone(true);
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
     * 更新单位信息 自动处理单位升级
     *
     * @param armyUnitIndexDTO 单位的index
     * @param updateCurr       是否更新当前单位
     * @return
     */
    public UnitStatusInfoDTO updateUnitInfo(ArmyUnitIndexDTO armyUnitIndexDTO) {
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);
        commandStream().toGameCommand().changeUnitStatus(unitStatusInfoDTO);
        return unitStatusInfoDTO;
    }

    /**
     * 有顺序的更新单位信息
     *
     * @param armyUnitIndexDTO 单位的index
     * @param updateCurr       是否更新当前单位
     * @return
     */
    public UnitStatusInfoDTO updateOrderUnitInfo(ArmyUnitIndexDTO armyUnitIndexDTO) {
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);
        commandStream().toGameCommand().changeUnitStatus(unitStatusInfoDTO);
        return unitStatusInfoDTO;
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


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

    }

}
