package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.List;

/**
 * 召唤师 判断有能力召唤
 */


public class SummonerActionHandle extends ActionHandle{

    private static SummonerActionHandle actionHandle = null;

    public static ActionHandle instance() {
        if (actionHandle == null) {
            actionHandle = new SummonerActionHandle();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Position> positions, UserRecord record, Integer camp, Integer unitIndex, Position aimPoint) {
        List<String> actions = super.getAction(positions, record, camp, unitIndex, aimPoint);
        List<Position> tombList = record.getTomb();
        if (tombList != null && tombList.size() > 0) {
            for (Position p : tombList) {
                if (positions.contains(p)) {
                    actions.add(ActionEnum.SUMMON.getType());
                    break;
                }
            }
        }
        return actions;
    }
}
