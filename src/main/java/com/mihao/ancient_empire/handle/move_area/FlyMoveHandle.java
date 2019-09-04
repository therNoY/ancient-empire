package com.mihao.ancient_empire.handle.move_area;

import com.mihao.ancient_empire.entity.mongo.UserRecord;

public class FlyMoveHandle extends MoveAreaHandle {

    private static FlyMoveHandle handel = null;

    public static FlyMoveHandle getInstance() {
        if (handel == null) {
            return new FlyMoveHandle();
        }
        return handel;
    }

    /**
     * 飞行对地形都是1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(UserRecord userRecord, int row, int column) {
        return 1;
    }
}
