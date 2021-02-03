package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 单位修复事件
 * @version 1.0
 * @author mihao
 * @date 2020\10\24 0024 15:52
 */
public class ClickRepairActionHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        currUnit().setColumn(currSite().getColumn());
        currUnit().setRow(currSite().getRow());
        Region region = new Region();
        region.setType(RegionEnum.TOWN.type());
        int index = getRegionIndexBySite(currSite());
        changeRegion(index, region);
        changeCurrRegion(index);
        endCurrentUnit(currUnitArmyIndex());
    }


}
