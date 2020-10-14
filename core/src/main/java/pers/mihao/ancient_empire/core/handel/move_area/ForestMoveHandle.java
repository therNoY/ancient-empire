package pers.mihao.ancient_empire.core.handel.move_area;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;

public class ForestMoveHandle extends MoveAreaHandle {

    private static ForestMoveHandle handel = null;

    public static ForestMoveHandle getInstance() {
        if (handel == null) {
            return new ForestMoveHandle();
        }
        return handel;
    }

    /**
     * 判断如果是森林就返回1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(UserRecord userRecord, int row, int column) {
        int mapColumn = userRecord.getGameMap().getColumn();
        List<Region> regionList = userRecord.getGameMap().getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        if (regionList.get(index).getType().equals(RegionEnum.FOREST.type()) || regionList.get(index).getType().equals(RegionEnum.GROVE.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
