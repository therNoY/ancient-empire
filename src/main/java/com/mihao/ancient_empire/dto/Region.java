package com.mihao.ancient_empire.dto;

/**
 * 代表地形的类
 */
public class Region extends Square {

    private Integer Buffer; // 地形增加的属性

    public Integer getBuffer() {
        return Buffer;
    }

    public void setBuffer(Integer buffer) {
        Buffer = buffer;
    }
}
