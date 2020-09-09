package pers.mihao.ancient_empire.core.handel.action;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

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
                    actions.add(ActionEnum.SUMMON.type());
                    break;
                }
            }
        }
        return actions;
    }
}
