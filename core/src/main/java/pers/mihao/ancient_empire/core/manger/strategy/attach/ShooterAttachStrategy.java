package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;

public class ShooterAttachStrategy extends AttachStrategy {

    private  String SHOOTER = "unitMes.shooter.buff";


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
    public AttributesPower getAttachPower(GameContext gameContext, UnitInfo attachInfo, UnitInfo beAttachUnitInfo, AttributesPower attributesPower) {
        for (Ability ability : beAttachUnitInfo.getAbilities()) {
            if (ability.getType().equals(AbilityEnum.FLY.type())) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(SHOOTER)));
                log.info("神射手 获取攻击加成{}", Integer.valueOf(AppConfig.get(SHOOTER)));
            }
        }
        return attributesPower;
    }
}
