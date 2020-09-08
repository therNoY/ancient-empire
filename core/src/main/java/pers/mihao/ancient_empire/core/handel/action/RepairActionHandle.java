package pers.mihao.ancient_empire.core.handel.action;

import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Position;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

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
            BaseSquare region = AppUtil.getRegionByPosition(record.getInitMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getInitMap().getColumn());
            if (region.getType().equals(RegionEnum.RUINS.type())) {
                actions.add(ActionEnum.REPAIR.type());
            }
        }

        return actions;
    }
}
