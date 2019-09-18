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
        if (!actions.contains(ActionEnum.OCCUPIED.getType())) {
            // 获取要移动到的地址
            BaseSquare region = AppUtil.getRegionByPosition(record.getInitMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getInitMap().getColumn());
            // 判断是城镇
            if (region.getType().equals(RegionEnum.TOWN.getType())){
                // 判断不是右方城镇
                Army army = null;
                if (StringUtil.isEmpty(region.getColor())) {
                    actions.add(ActionEnum.OCCUPIED.getType());
                    return actions;
                }
                if ((army = AppUtil.getArmyByColor(record, region.getColor())) != null) {
                    if (!army.getCamp().equals(camp)) {
                        actions.add(ActionEnum.OCCUPIED.getType());
                    }
                }
            }
        }
        return actions;
    }
}
