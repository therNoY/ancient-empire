package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;

public class SelectUnitResult extends ActiveResult {


    private Integer armyIndex;
    private Integer unitIndex;
    private String color;

    public SelectUnitResult(String recordId, Integer armyIndex, String color) {
        super.setRecordId(recordId);
        super.setResultEnum(AiActiveEnum.SELECT_UNIT);
        this.armyIndex = armyIndex;
        this.color = color;
    }

    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Integer getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(Integer unitIndex) {
        this.unitIndex = unitIndex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
