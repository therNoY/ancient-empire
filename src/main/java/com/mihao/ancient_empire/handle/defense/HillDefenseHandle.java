package com.mihao.ancient_empire.handle.defense;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.List;

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
        if (regionMes.getType().equals(RegionEnum.STONE.getType())) {
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
