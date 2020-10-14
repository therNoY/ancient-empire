package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\11 0011 18:58
 */
public class ArmyUnitIndexDTO implements Serializable {

    private Integer armyIndex;

    private Integer unitIndex;

    public ArmyUnitIndexDTO() {
    }

    public ArmyUnitIndexDTO(Integer armyIndex, Integer unitIndex) {
        this.armyIndex = armyIndex;
        this.unitIndex = unitIndex;
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

    @Override
    public String toString() {
        return "ArmyUnitIndexDTO{" +
                "armyIndex=" + armyIndex +
                ", unitIndex=" + unitIndex +
                '}';
    }
}
