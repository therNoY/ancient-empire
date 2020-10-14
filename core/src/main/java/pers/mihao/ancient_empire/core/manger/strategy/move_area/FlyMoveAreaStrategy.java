package pers.mihao.ancient_empire.core.manger.strategy.move_area;

import pers.mihao.ancient_empire.base.bo.GameMap;

public class FlyMoveAreaStrategy extends MoveAreaStrategy {


    /**
     * 飞行对地形都是1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(GameMap gameMap, int row, int column) {
        return 1;
    }
}
