package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

import java.util.ArrayList;
import java.util.List;


public class VillageGetActionStrategy extends ActionStrategy {

    private static VillageGetActionStrategy actionHandle = null;

    public static ActionStrategy instance() {
        if (actionHandle == null) {
            actionHandle = new VillageGetActionStrategy();
        }
        return actionHandle;
    }

    /**
     * 获取 有占领村庄能力者 是否可以能占领
     *
     * @param camp
     * @param unitIndex
     * @param sites     攻击范围
     * @param record
     * @param aimSite
     * @return
     */
    @Override
    public List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions = new ArrayList<>(1);
        // 获取要移动到的地址
        BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimSite.getRow(), aimSite.getColumn(), record.getGameMap().getColumn());
        // 判断是城镇
        if (region.getType().equals(RegionEnum.TOWN.type())) {
            // 判断不是右方城镇
            Army army;
            if (StringUtil.isBlack(region.getColor())) {
                actions.add(ActionEnum.OCCUPIED.type());
                return actions;
            }
            if ((army = AppUtil.getArmyByColor(record, region.getColor())) != null) {
                if (!army.getCamp().equals(record.getCurrCamp())) {
                    actions.add(ActionEnum.OCCUPIED.type());
                }
            }
        }
        return actions;
    }
}
