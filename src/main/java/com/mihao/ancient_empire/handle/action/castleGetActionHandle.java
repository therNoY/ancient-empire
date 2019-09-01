package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

/**
 * 城堡捕获者判断能否有能力占领 城堡
 */
public class castleGetActionHandle extends ActionHandle{

    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, String color, Integer unitIndex, Position aimPoint) {
        List<String> actions =  super.getAction(positions, record, color, unitIndex, aimPoint);
        if (!actions.contains(ActionEnum.OCCUPIED.getType())) {
            BaseSquare region = AppUtil.getRegionByPosition(record.getInitMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getInitMap().getColumn());
            if (region.getType().equals(RegionEnum.CASTLE.getType()) && !region.getColor().equals(color)) {
                actions.add(ActionEnum.OCCUPIED.getType());
            }
        }
        return actions;
    }
}
