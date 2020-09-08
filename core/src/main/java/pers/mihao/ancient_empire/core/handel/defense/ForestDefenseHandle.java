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

public class ForestDefenseHandle extends DefenseHandle {

    private static String FOREST = "unit.forestClose.buff";

    private static ForestDefenseHandle forestDefenseHandle = null;

    private ForestDefenseHandle() {
    }

    public static ForestDefenseHandle instance() {
        if (forestDefenseHandle == null) {
            forestDefenseHandle = new ForestDefenseHandle();
        }
        return forestDefenseHandle;
    }

    /**
     * 获取森林的防御
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
        if (regionMes.getType().equals(RegionEnum.FOREST.type()) || regionMes.getType().equals(RegionEnum.GROVE.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(FOREST)));
                log.info("森林之子 获取防御加成{}", Integer.valueOf(AppConfig.get(FOREST)));
            } else {
                int defense = super.getDefensePower(type, record, unit, levelMes, regionMes, beAttachUnit, attributesPower, beAttachAbility).getNum();
                attributesPower.setNum(defense + Integer.valueOf(AppConfig.get(FOREST)));
            }
        }
        return attributesPower;
    }
}
