package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
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
