package com.mihao.ancient_empire.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;

public enum UnitEnum implements BaseEnum {

    SOLDIER("战士"), WATER_ELEMENT("水元素");

    private String name;

    UnitEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
