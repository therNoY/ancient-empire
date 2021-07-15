package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mihao
 */
public class DestroyerActionStrategy extends ActionStrategy {

    private static DestroyerActionStrategy actionHandle = null;

    public static ActionStrategy instance() {
        if (actionHandle == null) {
            actionHandle = new DestroyerActionStrategy();
        }
        return actionHandle;
    }

    /**
     * 破化者可以破坏地形
     *
     * @param camp
     * @param unitIndex
     * @param sites 攻击范围
     * @param record
     * @param aimSite
     * @return
     */
    @Override
    public List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions = new ArrayList<>(1);

        if (actions.contains(ActionEnum.ATTACK.type())) {
            return actions;
        }

        List<Region> regions = record.getGameMap().getRegions();
        int column = record.getGameMap().getColumn();
        // 从所有的地形中
        for (int i = 0; i < regions.size(); i++) {
            BaseSquare region = regions.get(i);
            // 判断是敌方城镇

            if (region.getType().equals(RegionEnum.TOWN.type())) {
                Site site = AppUtil.getSiteByMapIndex(i, column);
                // 判断在攻击范围内
                if (sites.contains(site)) {
                    // 判断上面没有友军
                    if (!AppUtil.isFriend(record, site, record.getCurrCamp())) {
                        actions.add(ActionEnum.ATTACK.type());
                        break;
                    }
                }

            }
        }
        return actions;
    }
}
