package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

public class villageGetActionHandle extends ActionHandle{

    /**
     * 获取 有占领村庄能力者 是否可以能占领
     * @param positions 攻击范围
     * @param record
     * @param color
     * @param unitIndex
     * @param aimPoint
     * @return
     */
    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, String color, Integer unitIndex, Position aimPoint) {
        List<String> actions =  super.getAction(positions, record, color, unitIndex, aimPoint);
        if (!actions.contains(ActionEnum.OCCUPIED.getType())) {
            BaseSquare region = AppUtil.getRegionByPosition(record.getInitMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(),  record.getInitMap().getColumn());
            if (region.getType().equals(RegionEnum.TOWN.getType()) && !region.getColor().equals(color)) {
                actions.add(ActionEnum.OCCUPIED.getType());
            }
        }
        return actions;
    }
}
