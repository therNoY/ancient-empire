package pers.mihao.ancient_empire.core.manger.strategy.defense;

import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;

public class WaterDefenseStrategy extends DefenseStrategy {

    private static String WATER = "unitMes.waterClose.buff";

    /**
     * 获取水之子的攻击加成
     */
    @Override
    public AttributesPower getDefensePower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo, AttributesPower attributesPower) {
        String regionType = beAttachUnitInfo.getRegionInfo().getType();

        if (regionType.startsWith(RegionEnum.SEA.type()) || regionType.startsWith(RegionEnum.BANK.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(WATER)));
            log.info("水之子 获取防御加成{}", Integer.valueOf(AppConfig.get(WATER)));
        }
        return attributesPower;
    }
}
