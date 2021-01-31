package pers.mihao.ancient_empire.core.dto;

/**
 * 单位死亡dto
 * @version 1.0
 * @auther mihao
 * @date 2020\10\17 0017 16:47
 */
public class UnitDeadDTO extends ArmyUnitIndexDTO {

    public UnitDeadDTO() {
    }

    public UnitDeadDTO(ArmyUnitIndexDTO armyUnitIndexDTO) {
        super(armyUnitIndexDTO.getArmyIndex(), armyUnitIndexDTO.getUnitIndex());
    }

    public UnitDeadDTO(Integer armyIndex, Integer unitIndex) {
        super(armyIndex, unitIndex);
    }
}
