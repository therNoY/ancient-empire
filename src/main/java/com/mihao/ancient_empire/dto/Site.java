package com.mihao.ancient_empire.dto;

import java.io.Serializable;

public class Site implements Serializable {

    protected Integer row;
    protected Integer column;


    public Site() {
    }

    public Site(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Site) {
            Site that = (Site) obj;
            return row.equals(that.getRow()) && column.equals(that.getColumn());
        }
        return false;
    }

    @Override
    public String toString() {
        return "row=" + row + ", column=" + column;

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
}
