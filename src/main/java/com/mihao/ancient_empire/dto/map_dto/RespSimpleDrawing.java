package com.mihao.ancient_empire.dto.map_dto;

/**
 * 返回的优化后的结果
 */
public class RespSimpleDrawing {
    private Integer index;
    private String type;

    public RespSimpleDrawing(Integer index, String type) {
        this.index = index;
        this.type = type;
    }

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
}
