package pers.mihao.ancient_empire.core.dto;

public class ReqUnitIndexDto {
    private Integer armyIndex; // 军队的位置
    private Integer index; // 单位的位置

    public ReqUnitIndexDto() {
    }

    public ReqUnitIndexDto(Integer index) {
        this.index = index;
    }

    public ReqUnitIndexDto(Integer armyIndex, Integer index) {
        this.armyIndex = armyIndex;
        this.index = index;
    }

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
