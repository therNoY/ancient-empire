package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

public class destroyerActionHandle extends ActionHandle {

    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, String color, Integer unitIndex, Position aimPoint) {
        List<String> actions = super.getAction(positions, record, color, unitIndex, aimPoint);

        if (actions.contains(ActionEnum.ATTACK.getType())) {
            return actions;
        }

        List<BaseSquare> regions = record.getInitMap().getRegions();
        int column = record.getInitMap().getColumn();
        for (int i = 0; i < regions.size(); i++) {
            BaseSquare region = regions.get(i);
            if (region.getType().equals(RegionEnum.CASTLE.getType()) && !region.getColor().equals(color)) {
                if (positions.contains(AppUtil.getPositionByMapIndex(i, column))) {
                   actions.add(ActionEnum.ATTACK.getType());
                }
            }
        }
        return actions;
    }
}
