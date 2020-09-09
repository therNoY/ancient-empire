package pers.mihao.ancient_empire.core.handel.attach;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

public class MeleeMasterAttachHandle extends AttachHandle {

    private static String MELEE_MASTER = "unit.meleeMaster.buff";
    private static MeleeMasterAttachHandle meleeMasterAttachHandle = null;

    private MeleeMasterAttachHandle() {
    }

    public static MeleeMasterAttachHandle instance() {
        if (meleeMasterAttachHandle == null) {
            meleeMasterAttachHandle = new MeleeMasterAttachHandle();
        }
        return meleeMasterAttachHandle;
    }


    /**
     * 获取近战大师攻击
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        if (isReach(unit, beAttachUnit)) {
            Float addition = Float.valueOf(AppConfig.get(MELEE_MASTER));
            if (attributesPower.getAddition() != null) {
                if (addition > attributesPower.getAddition()) {
                    attributesPower.setAddition(addition);
                    log.info("近战大师 获取攻击加成{}倍", Integer.valueOf(AppConfig.get(MELEE_MASTER)));
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
