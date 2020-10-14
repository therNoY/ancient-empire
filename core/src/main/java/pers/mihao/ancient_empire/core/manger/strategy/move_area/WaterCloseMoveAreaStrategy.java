package pers.mihao.ancient_empire.core.manger.strategy.move_area;

import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.enums.RegionEnum;

import java.util.List;

public class WaterCloseMoveAreaStrategy extends MoveAreaStrategy {

    /**
     * 判断如果是水就返回 1
     * @param row
     * @param column
     * @return
     */
    @Override
    public int getRegionDeplete(GameMap gameMap, int row, int column) {
        int mapColumn = gameMap.getColumn();
        List<Region> regionList = gameMap.getRegions();
        int index = (row - 1) * mapColumn + column - 1;
        String type = regionList.get(index).getType();
        if (type.startsWith(RegionEnum.SEA.type()) || type.startsWith(RegionEnum.BANK.type())) {
            return 1;
        }else {
            return super.getRegionDeplete(gameMap, row, column);
        }
    }
}
