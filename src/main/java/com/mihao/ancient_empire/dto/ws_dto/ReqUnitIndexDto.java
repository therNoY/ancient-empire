package com.mihao.ancient_empire.dto.ws_dto;

public class ReqUnitIndexDto {
    private Integer armyIndex; // 军队的位置
    private Integer index; // 单位的位置

    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
