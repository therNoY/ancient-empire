package com.mihao.ancient_empire.dto;

/**
 * 建筑物类
 */
public class Building extends Region {

    private Integer armyId; // 所属军队的Id
    private Integer cash; // 每回合能增加的现金

    public Integer getArmyId() {
        return armyId;
    }

    public void setArmyId(Integer armyId) {
        this.armyId = armyId;
    }

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }
}
