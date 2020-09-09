package pers.mihao.ancient_empire.core.handel.defense;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

public class HillDefenseHandle extends DefenseHandle {

    private static String HILL = "unit.hillClose.buff";

    private static HillDefenseHandle hillDefenseHandle = null;

    private HillDefenseHandle() {
    }

    public static HillDefenseHandle instance() {
        if (hillDefenseHandle == null) {
            hillDefenseHandle = new HillDefenseHandle();
        }
        return hillDefenseHandle;
    }

    /**
     * 获取山头的攻击
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
        if (regionMes.getType().equals(RegionEnum.STONE.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(HILL)));
                log.info("山之子 获取防御加成{}", Integer.valueOf(AppConfig.get(HILL)));
            } else {
                int defense = super.getDefensePower(type, record, unit, levelMes, regionMes, beAttachUnit, attributesPower, beAttachAbility).getNum();
                attributesPower.setNum(defense + Integer.valueOf(AppConfig.get(HILL)));
            }
        }
        return attributesPower;
    }
}
