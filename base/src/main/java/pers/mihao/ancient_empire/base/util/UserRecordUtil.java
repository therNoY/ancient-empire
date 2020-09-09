package pers.mihao.ancient_empire.base.util;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;

/**
 * 改类是处理record 更改record 方便持久化更改的record
 */
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

    /**
     * 更新单位
     * @param record
     * @param id
     * @param unitHandle
     */
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
