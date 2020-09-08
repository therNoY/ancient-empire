package pers.mihao.ancient_empire.core.handel.defense;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

import java.util.List;

public class WaterDefenseHandle extends DefenseHandle {

    private static String WATER = "unit.waterClose.buff";
    private static WaterDefenseHandle waterDefenseHandle = null;

    private WaterDefenseHandle() {
    }

    public static WaterDefenseHandle instance() {
        if (waterDefenseHandle == null) {
            waterDefenseHandle = new WaterDefenseHandle();
        }
        return waterDefenseHandle;
    }

    /**
     * 获取水之子的攻击加成
     */
    @Override
    public AttributesPower getDefensePower(String type, UserRecord record, Unit unit, UnitLevelMes levelMes, RegionMes regionMes, Unit beAttachUnit, AttributesPower attributesPower, List<Ability> beAttachAbility) {
        if (regionMes.getType().startsWith(RegionEnum.SEA.type()) || regionMes.getType().startsWith(RegionEnum.BANK.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(WATER)));
                log.info("水之子 获取防御加成{}", Integer.valueOf(AppConfig.get(WATER)));
            } else {
                int defense = super.getDefensePower(type, record, unit, levelMes, regionMes, beAttachUnit, attributesPower, beAttachAbility).getNum();
                attributesPower.setNum(defense + Integer.valueOf(AppConfig.get(WATER)));
            }
        }
        return attributesPower;
    }
}
