package pers.mihao.ancient_empire.core.handel.move_area;

import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

import java.util.List;

public class HillMoveHandle extends MoveAreaHandle {

    private static HillMoveHandle handel = null;

    public static HillMoveHandle getInstance() {
        if (handel == null) {
            return new HillMoveHandle();
        }
        return handel;
    }

    /**
     * 判断如果是山就返回 1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(UserRecord userRecord, int row, int column) {
        int mapColumn = userRecord.getInitMap().getColumn();
        List<BaseSquare> regionList = userRecord.getInitMap().getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        if (regionList.get(index).getType().equals(RegionEnum.STONE.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
