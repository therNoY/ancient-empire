package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.util.EnumUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachHandle {

    Logger log = LoggerFactory.getLogger(this.getClass());

    protected AttachHandle() {
    }

    public static AttachHandle attachHandle = null;

    /**
     * 根据单位的 能力选择相应的能力处理器
     *
     * @param abilityType
     * @return
     */
    public static AttachHandle initAttachHandle(String abilityType) {
        AbilityEnum ability = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (ability) {
            case HILL_CLOSE:
                return HillAttachHandle.instance();
            case WATER_CLOSE:
                return WaterAttachHandle.instance();
            case FOREST_CLOSE:
                return ForestAttachHandle.instance();
            case SHOOTER:
                return ShooterAttachHandle.instance();
            case DESTROYER:
                return DestroyerAttachHandle.instance();
            case POISONING:
                return PoisoningAttachHandle.instance();
            case MELEE_MASTER:
                return MeleeMasterAttachHandle.instance();
            default:
                return getDefaultHandle();
        }
    }

    public static AttachHandle getDefaultHandle() {
        if (attachHandle == null) {
            return new AttachHandle();
        }
        return attachHandle;
    }


    /**
     * 获取攻击力
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        if (attributesPower.getNum() == null) {
            int attach = AppUtil.getAttachNum(levelMes);
            attributesPower.setNum(attach);
            log.info("{} 攻击 {} 基础攻击力是{}", unit.getType(), beAttachUnit.getType(), attach);
        }
        return attributesPower;
    }

    public int getAttach(AttributesPower attributesPower) {
        int attach = attributesPower.getNum();
        if (attributesPower.getAddition() != null) {
            attach = (int) (attach * attributesPower.getAddition());
        }
        return attach;
    }



}
