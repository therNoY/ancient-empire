package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 召唤师 判断有能力召唤
 */


public class SummonerActionStrategy extends ActionStrategy {

    private static SummonerActionStrategy actionHandle = null;

    public static ActionStrategy instance() {
        if (actionHandle == null) {
            actionHandle = new SummonerActionStrategy();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions = new ArrayList<>(1);
        List<Tomb> tombList = record.getTombList();
        if (tombList != null && tombList.size() > 0) {
            for (Site p : tombList) {
                if (sites.contains(p)) {
                    actions.add(ActionEnum.SUMMON.type());
                    break;
                }
            }
        }
        return actions;
    }
}
