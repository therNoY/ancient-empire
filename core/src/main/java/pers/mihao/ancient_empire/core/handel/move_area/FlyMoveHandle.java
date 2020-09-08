package pers.mihao.ancient_empire.core.handel.move_area;

import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

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
