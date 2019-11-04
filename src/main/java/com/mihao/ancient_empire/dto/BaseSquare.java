package com.mihao.ancient_empire.dto;

import java.io.Serializable;

public class BaseSquare implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String color;
    protected String type;


    public BaseSquare() {
    }

    public BaseSquare(String color, String type) {
        this.color = color;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
