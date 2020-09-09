package pers.mihao.ancient_empire.core.handel.move_area;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;

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
        int mapColumn = userRecord.getGameMap().getColumn();
        List<BaseSquare> regionList = userRecord.getGameMap().getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        if (regionList.get(index).getType().equals(RegionEnum.STONE.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
