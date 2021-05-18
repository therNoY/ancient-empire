package pers.mihao.ancient_empire.core.manger.strategy.end;

import pers.mihao.ancient_empire.common.util.Pair;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;

import java.util.List;

/**
 * 虚弱光环
 */
public class WeakerEndStrategy extends EndStrategy {


    @Override
    protected EndUnitDTO warpEndResult(List<Pair<Integer, Integer>> affectUnits, EndUnitDTO endUnitDTO, UserRecord record) {
        Unit unit;
        Army army;
        Integer armyIndex;
        Integer unitIndex;
        List<Ability> abilityList;
        // 净化者的deBuff能力
        for (Pair<Integer, Integer> pair : affectUnits) {
            unitIndex = pair.getValue();
            armyIndex = pair.getKey();
            army = record.getArmyList().get(armyIndex);
            unit = army.getUnits().get(unitIndex);
            abilityList = abilityService.getUnitAbilityList(unit.getTypeId());
            if (!abilityList.contains(AbilityEnum.WEAKER.ability()) && !army.getCamp().equals(record.getCurrCamp())) {
                // 修改单位的状态
                UnitStatusInfoDTO statusInfoDTO = new UnitStatusInfoDTO(armyIndex, unitIndex);
                statusInfoDTO.setStatus(StateEnum.WEAK.type());
                endUnitDTO.getUnitStatusInfoDTOS().add(statusInfoDTO);
            }
        }
        return endUnitDTO;
    }


}
