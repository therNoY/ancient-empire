package com.mihao.ancient_empire.dto.map_dto;

import com.mihao.ancient_empire.dto.BaseSquare;

import java.util.List;

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
