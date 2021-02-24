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
public class WaterCloseMovePathStrategy extends MovePathStrategy {

    /**
     * 判断如果是水就返回 1
     *
     * @return
     */
    @Override
    public int getRegionDeplete(GameMap gameMap, int index) {
        List<Region> regionList = gameMap.getRegions();
        String type = regionList.get(index).getType();
        if (type.startsWith(RegionEnum.SEA.type()) || type.startsWith(RegionEnum.BANK.type())) {
            return 1;
        } else {
            return super.getRegionDeplete(gameMap, index);
        }
    }
}
