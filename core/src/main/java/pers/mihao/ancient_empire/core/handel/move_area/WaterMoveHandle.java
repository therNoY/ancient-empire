package pers.mihao.ancient_empire.core.handel.move_area;

import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

import java.util.List;

public class WaterMoveHandle extends MoveAreaHandle {

    private static WaterMoveHandle handel = null;

    public static WaterMoveHandle getInstance() {
        if (handel == null) {
            return new WaterMoveHandle();
        }
        return handel;
    }

    /**
     * 判断如果是水就返回 1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(UserRecord userRecord, int row, int column) {
        int mapColumn = userRecord.getInitMap().getColumn();
        List<BaseSquare> regionList = userRecord.getInitMap().getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        String type = regionList.get(index).getType();
        if (type.startsWith(RegionEnum.SEA.type()) || type.startsWith(RegionEnum.BANK.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
