package com.mihao.ancient_empire.dto.mongo_dto;

import com.mihao.ancient_empire.dto.Unit;

import java.io.Serializable;

public class BuyUnitDto implements Serializable {
    private String uuid;
    private Unit unit;
    private Integer lastMoney;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Integer getLastMoney() {
        return lastMoney;
    }

    public void setLastMoney(Integer lastMoney) {
        this.lastMoney = lastMoney;
    }
}
