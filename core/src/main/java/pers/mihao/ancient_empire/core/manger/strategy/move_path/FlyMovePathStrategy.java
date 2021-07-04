package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 * @Author mihao
 * @Date 2020/9/16 13:49
 */
public class FlyMovePathStrategy extends MovePathStrategy {


    /**
     * 飞行对地形都是1
     * @return
     */
    @Override
    public int getRegionDeplete(GameMap gameMap, int index) {
        return 1;
    }
}
