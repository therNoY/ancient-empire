package pers.mihao.ancient_empire.core.manger.strategy.end;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.LifeChange;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class WeakerEndStrategy extends EndStrategy {

    private static WeakerEndStrategy endHandle = null;

    public static WeakerEndStrategy instance() {
        if (endHandle == null) {
            endHandle = new WeakerEndStrategy();
        }
        return endHandle;
    }

    @Override
    public RespEndResultDto getEndResult(RespEndResultDto respEndResultDto, UserRecord record, Unit cUnit) {

        Integer camp = AppUtil.getCurrentArmy(record).getCamp();

        Map<Integer, List<LifeChange>> lifeChanges = respEndResultDto.getLifeChanges();

        if (lifeChanges == null) {
            lifeChanges = new HashMap<>();
        }

        for (int j = 0; j < record.getArmyList().size(); j++) {
            Army army = record.getArmyList().get(j);
            List<Unit> units = army.getUnits();
            List<LifeChange> changeList = lifeChanges.get(army.getColor());
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                if (AppUtil.isAround(cUnit, unit)) {
                    if (!army.getCamp().equals(camp)) {
                        // 是敌军
                        if (changeList == null) {
                            changeList = new ArrayList<>();
                        }
                        LifeChange lifeChange = new LifeChange(i);
                        lifeChange.setState(StateEnum.WEAK.type());
                        changeList.add(lifeChange);
                    }
                }
            }
            lifeChanges.put(j, changeList);
        }

        respEndResultDto.setLifeChanges(lifeChanges);
        return respEndResultDto;
    }
}
