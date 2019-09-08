package com.mihao.ancient_empire.handle.defense;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.List;

public class RemoteDefenseHandle extends DefenseHandle {

    private static String REMOTE_DEFENSE = "unit.remoteDefense.buff";
    private static RemoteDefenseHandle remoteDefenseHandle = null;

    private RemoteDefenseHandle() {
    }

    public static RemoteDefenseHandle instance() {
        if (remoteDefenseHandle == null) {
            remoteDefenseHandle = new RemoteDefenseHandle();
        }
        return remoteDefenseHandle;
    }


    /**
     * 获取远程防御的防御
     *  @param type
     * @param record
     * @param unit
     * @param levelMes
     * @param regionMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getDefensePower(String type, UserRecord record, Unit unit, UnitLevelMes levelMes, RegionMes regionMes, Unit beAttachUnit, AttributesPower attributesPower, List<Ability> beAttachAbility) {
        if (!isReach(unit, beAttachUnit)) {
            Float addition = Float.valueOf(AppConfig.get(REMOTE_DEFENSE));
            if (attributesPower.getAddition() != null) {
                if (addition > attributesPower.getAddition()) {
                    attributesPower.setAddition(addition);
                }
            } else {
                attributesPower.setAddition(addition);
            }
        }
        return attributesPower;
    }

    /**
     * 判断两点是否可达
     *
     * @param currP
     * @param aimP
     * @return
     */
    private boolean isReach(Unit currP, Unit aimP) {
        if (Math.abs(currP.getRow() - aimP.getRow()) + Math.abs(currP.getColumn() - aimP.getColumn()) == 1) {
            return true;
        }
        return false;
    }
}
