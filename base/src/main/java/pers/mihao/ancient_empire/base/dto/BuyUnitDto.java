package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;
import pers.mihao.ancient_empire.base.bo.Unit;

public class BuyUnitDto implements Serializable {
    private String uuid;
    private Unit unit;
    private Integer lastMoney;
    private Integer endPop;

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

    public Integer getEndPop() {
        return endPop;
    }

    public void setEndPop(Integer endPop) {
        this.endPop = endPop;
    }
}
