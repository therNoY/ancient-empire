package pers.mihao.ancient_empire.core.handel.attach;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

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
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(beAttachUnit.getType());
        for (Ability ability : abilityList) {
            if (ability.getType().equals(AbilityEnum.FLY.type())) {
                if (attributesPower.getNum() != null) {
                    attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(SHOOTER)));
                    log.info("神射手 获取攻击加成{}", Integer.valueOf(AppConfig.get(SHOOTER)));
                } else {
                    int attach = GameCoreHelper.getAttachNum(levelMes);
                    attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(SHOOTER)));
                }
            }
        }
        return attributesPower;
    }
}
