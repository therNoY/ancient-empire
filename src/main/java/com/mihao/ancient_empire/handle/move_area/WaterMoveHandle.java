package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

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
        if (type.startsWith(RegionEnum.SEA.getType()) || type.startsWith(RegionEnum.BANK.getType())) {
            return 1;
        }else {
            return super.getRegionDeplete(userRecord, row, column);
        }
    }
}
