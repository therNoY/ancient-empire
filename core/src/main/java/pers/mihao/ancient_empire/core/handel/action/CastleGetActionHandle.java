package pers.mihao.ancient_empire.core.handel.action;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

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
            BaseSquare region = AppUtil
                .getRegionByPosition(record.getGameMap().getRegions(), aimPoint.getRow(), aimPoint.getColumn(), record.getGameMap().getColumn());
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