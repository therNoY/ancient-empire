package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 修理者能力者 判断是否有可以修理的房子
 * @author mihao
 */
public class RepairActionStrategy extends ActionStrategy {

    private static RepairActionStrategy actionHandle = null;

    public static ActionStrategy instance() {
        if (actionHandle == null) {
            actionHandle = new RepairActionStrategy();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions = new ArrayList<>(1);
        BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimSite.getRow(), aimSite.getColumn(), record.getGameMap().getColumn());
        if (region.getType().equals(RegionEnum.RUINS.type())) {
            actions.add(ActionEnum.REPAIR.type());
        }

        return actions;
    }
}
