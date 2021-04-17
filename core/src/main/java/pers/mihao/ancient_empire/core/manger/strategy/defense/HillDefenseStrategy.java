package pers.mihao.ancient_empire.core.manger.strategy.defense;

import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;

public class HillDefenseStrategy extends DefenseStrategy {

    private static String HILL = "unitMes.hillClose.buff";

    /**
     * 获取山之子的攻击
     * @param gameContext
     * @param attachUnitInfo
     * @param beAttachUnitInfo
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getDefensePower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo, AttributesPower attributesPower) {
        String regionType = beAttachUnitInfo.getRegionInfo().getType();
        if (regionType.equals(RegionEnum.STONE.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(HILL)));
            log.info("山之子 获取防御加成{}", Integer.valueOf(AppConfig.get(HILL)));

        }
        return attributesPower;
    }
}
