package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

public class PathPosition {
    private Integer row;
    private Integer column;
    private Integer length;

    public PathPosition(Position position) {
        this.row = position.getRow();
        this.column = position.getColumn();
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

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
