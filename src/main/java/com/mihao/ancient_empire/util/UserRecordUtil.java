package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

public class UserRecordUtil {

    public static void updateCurrUnit(UserRecord record, String id, UnitHandle unitHandle) {

        for (Army army : record.getArmyList()) {
            if (army.getColor() == record.getCurrColor()) {
                for (Unit unit : army.getUnits()) {
                    if (unit.getId().equals(id)) {
                        unitHandle.handle(unit);
                        return;
                    }
                }
            }
        }

    }

    public static void updateUnit(UserRecord record, String id, UnitHandle unitHandle) {
        for (Army army : record.getArmyList()) {
            if (army.getColor() != record.getCurrColor()) {
                for (Unit unit : army.getUnits()) {
                    if (unit.getId().equals(id)) {
                        unitHandle.handle(unit);
                        return;
                    }
                }
            }
        }
    }
}
