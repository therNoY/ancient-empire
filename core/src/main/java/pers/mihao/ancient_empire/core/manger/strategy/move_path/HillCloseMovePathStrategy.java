package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 * @Author mh32736
 * @Date 2020/9/16 13:49
 */
public class HillCloseMovePathStrategy extends MovePathStrategy {

    /**
     * 判断如果是山就返回 1
     * @return
     */
    @Override
    public int getRegionDeplete(GameMap gameMap, int index) {
        List<Region> regionList = gameMap.getRegions();
        if (regionList.get(index).getType().equals(RegionEnum.STONE.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(gameMap, index);
        }
    }
}
