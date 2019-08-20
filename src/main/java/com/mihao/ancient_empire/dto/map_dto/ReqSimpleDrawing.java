package com.mihao.ancient_empire.dto.map_dto;

import com.mihao.ancient_empire.dto.BaseSquare;

import java.util.List;

public class ReqSimpleDrawing {
    private Integer index;
    private String type;
    private Integer row;
    private Integer column;
    List<BaseSquare> regionList;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public List<BaseSquare> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<BaseSquare> regionList) {
        this.regionList = regionList;
    }
}
