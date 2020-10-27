package pers.mihao.ancient_empire.core.manger.handler;

import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 通用处理类 一些通用发出命令的方法 本身不处理事件
 * @version 1.0
 * @auther mihao
 * @date 2020\10\4 0004 8:19
 */
public class CommonHandler extends BaseHandler{

    /**
     * 改变当前点
     * @param site
     */
    public void changeCurrPoint(Site site){
        // 设置当前点
        record().setCurrPoint(site);
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_POINT, site);
    }


    /**
     * 改变当前单位
     * @param site
     */
    public Pair<Integer, UnitInfo> changeCurrUnit(Site site){
        // 设置当前单位
        Pair<Integer, UnitInfo> unitInfoPair = getUnitInfoFromMapBySite(site);
        record().setCurrUnit(unitInfoPair.getValue());
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, ExtMes.UNIT_INFO, unitInfoPair.getValue());
        return unitInfoPair;
    }

    /**
     * 获取unitInfo 通过位置获取
     * @param site
     */
    public Pair<Integer, UnitInfo> getUnitInfoFromMapBySite(Site site){
        // 设置当前单位
        Pair<Integer, Unit> unitMes = getUnitFromMapBySite(site);
        UnitInfo unitInfo = unitMesService.getUnitInfo(unitMes.getValue().getType(), unitMes.getValue().getLevel());

        BeanUtil.copyValueFromParent(unitMes.getValue(), unitInfo);
        unitInfo.setRegionInfo(getRegionInfoBySite(unitInfo.getRow(), unitInfo.getColumn()));
        return new Pair<>(unitMes.getKey(), unitInfo);
    }

    /**
     * 改变当前地形
     * @param site
     */
    public RegionInfo changeCurrRegion(Site site){
        // 设置当前地形
        Region region = getRegionBySite(site);
        return changeCurrRegion(region);
    }


    /**
     * 改变当前地形
     * @param regionIndex
     */
    public RegionInfo changeCurrRegion(Integer regionIndex){
        // 设置当前地形
        Region region = gameMap().getRegions().get(regionIndex);
        return changeCurrRegion(region);
    }

    private RegionInfo changeCurrRegion(Region region) {
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        record().setCurrRegion(regionInfo);

        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, ExtMes.REGION_INFO, regionInfo);
        return regionInfo;
    }


    /**
     * 根据Site 获取regionInfo
     * @param row
     * @param column
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
     * 改变当前颜色
     * @param color
     */
    public void changeCurrBgColor(String color){
        gameContext.setBgColor(color);
        // 设置前端执行指令
        commandStream()
                .toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_BG_COLOR, ExtMes.BG_COLOR, gameContext.getBgColor());
    }


    /**
     * 更新单位信息
     * @param armyUnitIndexDTO 单位的index
     * @param armyUnitIndexDTO 是否更新当前单位
     * @return
     */
    public UnitStatusInfoDTO updateUnitInfo(ArmyUnitIndexDTO armyUnitIndexDTO){
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_UNIT_STATUS, ExtMes.UNIT_STATUS, unitStatusInfoDTO);
        return unitStatusInfoDTO;
    }

    /**
     * 有顺序的更新单位信息
     * @param armyUnitIndexDTO 单位的index
     * @return
     */
    public UnitStatusInfoDTO updateOrderUnitInfo(ArmyUnitIndexDTO armyUnitIndexDTO){
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(armyUnitIndexDTO);
        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_UNIT_STATUS, ExtMes.UNIT_STATUS, unitStatusInfoDTO);
        return unitStatusInfoDTO;
    }

    public UnitStatusInfoDTO updateUnitInfo(){
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO();
        commandStream().toGameCommand().addCommand(GameCommendEnum.CHANGE_UNIT_STATUS, ExtMes.UNIT_STATUS, unitStatusInfoDTO);
        return unitStatusInfoDTO;
    }


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

    }

}
