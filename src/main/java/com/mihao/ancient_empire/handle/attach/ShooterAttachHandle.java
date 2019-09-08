package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;

import java.util.List;

public class ShooterAttachHandle extends AttachHandle {

    private static String SHOOTER = "unit.shooter.buff";

    private static ShooterAttachHandle shooterAttachHandle = null;

    private AbilityService abilityService = ApplicationContextHolder.getBean(AbilityService.class);

    private ShooterAttachHandle() {
    }

    public static ShooterAttachHandle instance() {
        if (shooterAttachHandle == null) {
            shooterAttachHandle = new ShooterAttachHandle();
        }
        return shooterAttachHandle;
    }

    /**
     * 获取射手攻击
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(beAttachUnit.getType());
        for (Ability ability : abilityList) {
            if (ability.getType().contains(AbilityEnum.FLY.getType())) {
                if (attributesPower.getNum() != null) {
                    attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(SHOOTER)));
                    log.info("神射手 获取攻击加成{}倍", Integer.valueOf(AppConfig.get(SHOOTER)));
                } else {
                    int attach = AppUtil.getAttachNum(levelMes);
                    attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(SHOOTER)));
                }
            }
        }
        return attributesPower;
    }
}
