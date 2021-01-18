package pers.mihao.ancient_empire.core.handel.action;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;


public class VillageGetActionHandle extends ActionHandle {

    private static VillageGetActionHandle actionHandle = null;

    public static ActionHandle instance() {
        if (actionHandle == null) {
            actionHandle = new VillageGetActionHandle();
        }
        return actionHandle;
    }

    /**
     * 获取 有占领村庄能力者 是否可以能占领
     *
     * @param positions 攻击范围
     * @param record
     * @param camp
     * @param unitIndex
     * @param aimPoint
     * @return
     */
    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, Integer camp, Integer unitIndex, Position aimPoint) {
        List<String> actions = super.getAction(positions, record, camp, unitIndex, aimPoint);
        if (!actions.contains(ActionEnum.OCCUPIED.type())) {
            // 获取要移动到的地址
            BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getGameMap().getColumn());
            // 判断是城镇
            if (region.getType().equals(RegionEnum.TOWN.type())){
                // 判断不是右方城镇
                Army army = null;
                if (StringUtil.isBlack(region.getColor())) {
                    actions.add(ActionEnum.OCCUPIED.type());
                    return actions;
                }
                if ((army = AppUtil.getArmyByColor(record, region.getColor())) != null) {
                    if (!army.getCamp().equals(camp)) {
                        actions.add(ActionEnum.OCCUPIED.type());
                    }
                }
            }
        }
        return actions;
    }
}
