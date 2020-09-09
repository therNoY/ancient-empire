package pers.mihao.ancient_empire.core.handel.action;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

/**
 * 修理者能力者 判断是否有可以修理的房子
 */

public class RepairActionHandle extends ActionHandle {

    private static RepairActionHandle actionHandle = null;

    public static ActionHandle instance() {
        if (actionHandle == null) {
            actionHandle = new RepairActionHandle();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, Integer camp, Integer unitIndex, Position aimPoint) {
        List<String> actions =  super.getAction(positions, record, camp, unitIndex, aimPoint);
        if (!actions.contains(ActionEnum.REPAIR.type())) {
            BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getGameMap().getColumn());
            if (region.getType().equals(RegionEnum.RUINS.type())) {
                actions.add(ActionEnum.REPAIR.type());
            }
        }

        return actions;
    }
}
