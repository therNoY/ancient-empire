package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

/**
 * 城堡捕获者判断能否有能力占领 城堡
 */
public class CastleGetActionHandle extends ActionHandle{

    private static CastleGetActionHandle actionHandle = null;

    public static ActionHandle instance() {
        if (actionHandle == null) {
            actionHandle = new CastleGetActionHandle();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, Integer camp, Integer unitIndex, Position aimPoint) {
        List<String> actions =  super.getAction(positions, record, camp, unitIndex, aimPoint);
        if (!actions.contains(ActionEnum.OCCUPIED.type())) {
            BaseSquare region = AppUtil.getRegionByPosition(record.getInitMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getInitMap().getColumn());
            if (region.getType().equals(RegionEnum.CASTLE.type())){
                // 判断不是右方城镇
                Army army = null;
                if (StringUtil.isEmpty(region.getColor())) {
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
