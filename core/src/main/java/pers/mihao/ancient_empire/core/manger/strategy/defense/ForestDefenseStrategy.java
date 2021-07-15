package pers.mihao.ancient_empire.core.manger.strategy.defense;

import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @author mihao
 */
public class ForestDefenseStrategy extends DefenseStrategy {

    private static String FOREST = "unitMes.forestClose.buff";

    /**
     * 获取森林之子的防御
     *
     * @param gameContext
     * @param attachUnitInfo
     * @param beAttachUnitInfo
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getDefensePower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo,
        AttributesPower attributesPower) {
        String regionType = beAttachUnitInfo.getRegionInfo().getType();
        if (regionType.equals(RegionEnum.FOREST.type()) || regionType.equals(RegionEnum.GROVE.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(FOREST)));
            log.info("森林之子 获取防御加成{}", Integer.valueOf(AppConfig.get(FOREST)));
        }
        return attributesPower;
    }
}
