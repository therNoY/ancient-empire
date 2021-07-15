package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @author mihao
 */
public class DestroyerAttachStrategy extends AttachStrategy {

    private String DESTROYER = "unitMes.destroyer.buff";

    /**
     * 破化者的攻击加成
     *
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(GameContext gameContext, UnitInfo attachInfo, UnitInfo beAttachUnitInfo,
        AttributesPower attributesPower) {
        RegionInfo region = beAttachUnitInfo.getRegionInfo();
        if (region.getType().equals(RegionEnum.TOWN.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(DESTROYER)));
            log.info("破化者获取攻击加成{}", Integer.valueOf(AppConfig.get(DESTROYER)));
        }
        return attributesPower;
    }
}
