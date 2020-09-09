package pers.mihao.ancient_empire.core.handel.attach;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

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
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit,
        AttributesPower attributesPower) {
        if (!StringUtil.isEmpty(beAttachUnit.getStatus())) {
            if (beAttachUnit.getStatus().equals(StateEnum.EXCITED.type())) {
                if (attributesPower.getNum() != null) {
                    attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(POISONING)));
                    log.info("投毒者 获取攻击加成{}", Integer.valueOf(AppConfig.get(POISONING)));
                } else {
                    int attach = GameCoreHelper.getAttachNum(levelMes);
                    attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(POISONING)));
                }
            }
        }
        return attributesPower;
    }
}
