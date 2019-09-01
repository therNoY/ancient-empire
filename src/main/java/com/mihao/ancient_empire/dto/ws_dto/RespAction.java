package com.mihao.ancient_empire.dto.ws_dto;

/**
 * 返回单位移动后可进行的行动
 */
public class RespAction {
    private String name;
    private Float row;
    private Float column;

    public RespAction() {
    }

    public RespAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getRow() {
        return row;
    }

    public void setRow(Float row) {
        this.row = row;
    }

    public Float getColumn() {
        return column;
    }

    public void setColumn(Float column) {
        this.column = column;
    }
}
