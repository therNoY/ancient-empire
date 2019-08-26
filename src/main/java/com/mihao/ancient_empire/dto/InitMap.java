package com.mihao.ancient_empire.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 地图类
 */
public class InitMap implements Serializable {
    private static final long serialVersionUID = 1L;
    // 宽
    private Integer row;
    // 高
    private Integer column;
    // 一个n*m 的区域组成的地图
    List<BaseSquare> regions;

    public InitMap() {
    }

    public InitMap(Integer row, Integer column, List<BaseSquare> regions) {
        this.row = row;
        this.column = column;
        this.regions = regions;
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

    public List<BaseSquare> getRegions() {
        return regions;
    }

    public void setRegions(List<BaseSquare> regions) {
        this.regions = regions;
    }
}
