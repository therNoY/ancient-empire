package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

import java.util.List;

/**
 * 城堡捕获者判断能否有能力占领 城堡
 */
public class CastleGetActionStrategy extends ActionStrategy {

    private static CastleGetActionStrategy actionHandle = null;

    public static ActionStrategy instance() {
        if (actionHandle == null) {
            actionHandle = new CastleGetActionStrategy();
        }
        return actionHandle;
    }

    @Override
    public List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions =  super.getAction(sites, record, aimSite);
        if (!actions.contains(ActionEnum.OCCUPIED.type())) {
            BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimSite.getRow(), aimSite.getColumn(), record.getGameMap().getColumn());
            if (region.getType().equals(RegionEnum.CASTLE.type())){
                // 判断不是右方城镇
                Army army = null;
                if (StringUtil.isEmpty(region.getColor())) {
                    actions.add(ActionEnum.OCCUPIED.type());
                    return actions;
                }
                if ((army = AppUtil.getArmyByColor(record, region.getColor())) != null) {
                    if (!army.getCamp().equals(record.getCurrCamp())) {
                        actions.add(ActionEnum.OCCUPIED.type());
                    }
                }
            }
        }
        return actions;
    }
}
