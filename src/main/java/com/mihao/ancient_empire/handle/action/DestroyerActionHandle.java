package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;


public class DestroyerActionHandle extends ActionHandle {

    private static DestroyerActionHandle actionHandle = null;

    public static ActionHandle instance() {
        if (actionHandle == null) {
            actionHandle = new DestroyerActionHandle();
        }
        return actionHandle;
    }

    /**
     * 破化者可以破坏地形
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

        if (actions.contains(ActionEnum.ATTACK.getType())) {
            return actions;
        }

        List<BaseSquare> regions = record.getInitMap().getRegions();
        int column = record.getInitMap().getColumn();
        // 从所有的地形中
        for (int i = 0; i < regions.size(); i++) {
            BaseSquare region = regions.get(i);
            // 判断是敌方城镇

            if (region.getType().equals(RegionEnum.TOWN.getType())) {
                Position position = AppUtil.getPositionByMapIndex(i, column);
                // 判断在攻击范围内
                if (positions.contains(position)) {
                    // 判断上面没有友军
                    if (!AppUtil.isFriend(record, position, camp)) {
                        actions.add(ActionEnum.ATTACK.getType());
                        break;
                    }
                }

            }
        }
        return actions;
    }
}
