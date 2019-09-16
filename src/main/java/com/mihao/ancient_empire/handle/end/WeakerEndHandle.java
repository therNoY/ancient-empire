package com.mihao.ancient_empire.handle.end;

import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.LifeChange;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
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
                        lifeChange.setState(StateEnum.WEAK.getType());
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
