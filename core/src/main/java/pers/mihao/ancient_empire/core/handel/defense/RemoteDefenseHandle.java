package pers.mihao.ancient_empire.core.handel.defense;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

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
                    log.info("远程防御 获取防御加成{} 倍", Integer.valueOf(AppConfig.get(REMOTE_DEFENSE)));
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
