package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;

public class ReqTempMapDto {

    List<Region> regionList;

    List<Region> unitList;

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

    public List<Region> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Region> unitList) {
        this.unitList = unitList;
    }
}
