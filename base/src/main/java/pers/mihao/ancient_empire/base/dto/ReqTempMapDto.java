package pers.mihao.ancient_empire.base.dto;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;

public class ReqTempMapDto {

    List<BaseSquare> regionList;

    List<BaseSquare> unitList;

    public List<BaseSquare> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<BaseSquare> regionList) {
        this.regionList = regionList;
    }

    public List<BaseSquare> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<BaseSquare> unitList) {
        this.unitList = unitList;
    }
}
