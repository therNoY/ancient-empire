package pers.mihao.ancient_empire.base.bo;

import java.io.Serializable;

/**
 * 基础的块元素 表示有个位置
 */
public class BaseSquare implements Serializable {

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
