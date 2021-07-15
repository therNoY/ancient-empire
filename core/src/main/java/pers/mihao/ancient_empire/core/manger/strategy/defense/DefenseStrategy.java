package pers.mihao.ancient_empire.core.manger.strategy.defense;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

/**
 * 根据不同的能力判断防御力加成
 *
 * @author mihao
 */
public class DefenseStrategy extends AbstractStrategy<DefenseStrategy> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    protected DefenseStrategy() {
    }

    public static final String PHYSICAL_DEFENSE = "1";
    public static final String MAGIC_DEFENSE = "2";

    public static DefenseStrategy defenseStrategy = null;


    public static DefenseStrategy getInstance() {
        if (defenseStrategy == null) {
            defenseStrategy = new DefenseStrategy();
        }
        return defenseStrategy;
    }

    /**
     * 获取单位的攻击力
     *
     * @param gameContext
     * @param attachUnitInfo
     * @param beAttachUnitInfo
     * @return
     */
    public AttributesPower getUnitDefenseInfo(GameContext gameContext, UnitInfo attachUnitInfo,
        UnitInfo beAttachUnitInfo) {
        AttributesPower attributesPower = new AttributesPower();
        getDefensePower(gameContext, attachUnitInfo, beAttachUnitInfo, attributesPower);
        log.info("{} 攻击 {} >>> 基础防御力是{}", attachUnitInfo.getType(), beAttachUnitInfo.getType(),
            attributesPower.getNum());
        getAbilityStrategy(beAttachUnitInfo.getAbilities()).forEach(attachStrategy -> {
            attachStrategy.getDefensePower(gameContext, attachUnitInfo, beAttachUnitInfo, attributesPower);
        });
        return attributesPower;
    }

    /**
     * 获取单位单位的初始防御 根据等级获得
     *
     * @param gameContext
     * @param attachUnitInfo
     * @param beAttachUnitInfo
     * @param attributesPower
     * @return
     */
    public AttributesPower getDefensePower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo,
        AttributesPower attributesPower) {
        // 不为空在设
        if (attributesPower.getNum() == null) {
            int defense;
            // 判断是物理防御还是魔法防御
            if (attachUnitInfo.getUnitMes().getAttackType().equals(PHYSICAL_DEFENSE)) {
                defense = beAttachUnitInfo.getLevelMes().getPhysicalDefense();
                log.info("{} 初始物理防御 {}", beAttachUnitInfo.getType(), defense);
            } else {
                defense = beAttachUnitInfo.getLevelMes().getMagicDefense();
                log.info("{} 初始魔法防御 {}", beAttachUnitInfo.getType(), defense);
            }
            attributesPower.setNum(defense);
        }
        return attributesPower;
    }
}
