package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.List;

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
        int mapColumn = userRecord.getInitMap().getColumn();
        List<BaseSquare> regionList = userRecord.getInitMap().getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        if (regionList.get(index).getType().equals(RegionEnum.FOREST.getType()) || regionList.get(index).getType().equals(RegionEnum.GROVE.getType())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
