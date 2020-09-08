package pers.mihao.ancient_empire.core.handel.end;

import pers.mihao.ancient_empire.common.constant.StateEnum;
import pers.mihao.ancient_empire.common.bo.Army;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.LifeChange;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class WeakerEndHandle extends EndHandle{

    private static WeakerEndHandle endHandle = null;

    public static WeakerEndHandle instance() {
        if (endHandle == null) {
            endHandle = new WeakerEndHandle();
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
