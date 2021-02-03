package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 点击占领
 * @version 1.0
 * @author mihao
 * @date 2020\10\24 0024 16:01
 */
public class ClickOccupiedActionHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        currUnit().setColumn(currSite().getColumn());
        currUnit().setRow(currSite().getRow());
        Region region = new Region();
        region.setColor(currArmy().getColor());
        int index = getRegionIndexBySite(currSite());
        region.setType(gameMap().getRegions().get(index).getType());
        changeRegion(index, region);
        changeCurrRegion(index);
        endCurrentUnit(currUnitArmyIndex());
    }

}
