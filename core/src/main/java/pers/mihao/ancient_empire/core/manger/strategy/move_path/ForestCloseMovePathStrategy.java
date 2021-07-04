package pers.mihao.ancient_empire.core.manger.strategy.move_path;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 * @Author mihao
 * @Date 2020/9/16 13:49
 */
public class ForestCloseMovePathStrategy extends MovePathStrategy {


    /**
     * 判断如果是森林就返回1
     * @return
     */
    @Override
    protected int getRegionDeplete(GameMap gameMap, int index) {
        List<Region> regionList = gameMap.getRegions();
        if (regionList.get(index).getType().equals(RegionEnum.FOREST.type()) || regionList.get(index).getType().equals(RegionEnum.GROVE.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(gameMap, index);
        }
    }
}
