package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

public class PoisoningAttachHandle extends AttachHandle {

    private static String POISONING = "unit.poisoning.buff";
    private static PoisoningAttachHandle poisoningAttachHandle = null;

    private PoisoningAttachHandle() {
    }

    public static PoisoningAttachHandle instance() {
        if (poisoningAttachHandle == null) {
            poisoningAttachHandle = new PoisoningAttachHandle();
        }
        return poisoningAttachHandle;
    }

    /**
     * 获取投毒者攻击
     *
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        if (!StringUtil.isEmpty(beAttachUnit.getStatus())) {
            if (beAttachUnit.getStatus().equals(StateEnum.EXCITED.getType())) {
                if (attributesPower.getNum() != null) {
                    attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(POISONING)));
                } else {
                    int attach = AppUtil.getAttachNum(levelMes);
                    attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(POISONING)));
                }
            }
        }
        return attributesPower;
    }
}
