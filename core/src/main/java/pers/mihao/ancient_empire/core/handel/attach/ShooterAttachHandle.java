package pers.mihao.ancient_empire.core.handel.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.AbilityEnum;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.auth.service.AbilityService;
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
            if (ability.getType().equals(AbilityEnum.FLY.type())) {
                if (attributesPower.getNum() != null) {
                    attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(SHOOTER)));
                    log.info("神射手 获取攻击加成{}", Integer.valueOf(AppConfig.get(SHOOTER)));
                } else {
                    int attach = AppUtil.getAttachNum(levelMes);
                    attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(SHOOTER)));
                }
            }
        }
        return attributesPower;
    }
}
