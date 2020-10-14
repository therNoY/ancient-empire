package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.enums.RegionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\20 0020 21:10
 */
public interface MapService {

    default List<Site> findCastleTitle(List<Region> regions, Integer row, Integer column){
        List<Site> sites = new ArrayList<>();
        BaseSquare region;
        // 获取所有的城堡index 然后设置绝对定位设置城堡的头部
        for (int index = 0; index < regions.size(); index++) {
            region = regions.get(index);
            if (RegionEnum.CASTLE.type().equals(region.getType())) {
                Site castleTitle = new Site();
                if ((index + 1) % row == 0) {
                    castleTitle.setRow((index + 1) / column - 1);;
                    castleTitle.setColumn(column);
                } else {
                    castleTitle.setRow((index + 1) / column);
                    castleTitle.setColumn((index + 1) % column);
                }
                sites.add(castleTitle);
            }
        }
        return sites;
    }

}
