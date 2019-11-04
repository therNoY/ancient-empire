package com.mihao.ancient_empire.handle.defense;

import com.mihao.ancient_empire.common.util.EnumUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttachSituation;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 根据不同的能力判断防御力加成
 */
public class DefenseHandle {

    Logger log = LoggerFactory.getLogger(this.getClass());

    protected DefenseHandle() {
    }

    public static final String PHYSICAL_DEFENSE = "1";
    public static final String MAGIC_DEFENSE = "2";
    public static DefenseHandle defenseHandle = null;

    /**
     * 根据单位的 能力选择相应的能力处理器
     *
     * @param abilityType
     * @return
     */
    public static DefenseHandle initAttachHandle(String abilityType) {
        AbilityEnum ability = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (ability) {
            case HILL_CLOSE:
                return HillDefenseHandle.instance();
            case WATER_CLOSE:
                return WaterDefenseHandle.instance();
            case FOREST_CLOSE:
                return ForestDefenseHandle.instance();
            case REMOTE_DEFENSE:
                return RemoteDefenseHandle.instance();
            default:
                return getDefaultHandle();
        }
    }

    public static DefenseHandle getDefaultHandle() {
        if (defenseHandle == null) {
            return new DefenseHandle();
        }
        return defenseHandle;
    }

    public AttributesPower getDefensePower(String type, UserRecord record, Unit unit, UnitLevelMes levelMes, RegionMes regionMes, Unit beAttachUnit, AttributesPower attributesPower, List<Ability> beAttachAbility) {
        // 不为空在设
        if (attributesPower.getNum() == null) {
            int defense;
            // 判断是物理防御还是魔法防御
            if (type.equals(PHYSICAL_DEFENSE)) {
                defense = levelMes.getPhysicalDefense();
                log.info("{} 初始物理防御{}", beAttachUnit.getType(), defense);
            } else {
                defense = levelMes.getMagicDefense();
                log.info("{} 初始魔法防御{}", beAttachUnit.getType(), defense);
            }

            attributesPower.setNum(defense);
        }

        return attributesPower;
    }

    public int getDefense(AttributesPower defensePower) {
        int defense = defensePower.getNum();
        if (defensePower.getAddition() != null) {
            defense = (int) (defense * defensePower.getAddition());
        }
        return defense;
    }
}
