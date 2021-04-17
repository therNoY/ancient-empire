package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;

public class WaterCloseAttachStrategy extends AttachStrategy {

    private String WATER = "unitMes.waterClose.buff";

    /**
     * 获取水之子的攻击策略
     *
     * @param gameContext
     * @param attachInfo
     * @param beAttachUnitInfo
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(GameContext gameContext, UnitInfo attachInfo, UnitInfo beAttachUnitInfo, AttributesPower attributesPower) {
        RegionInfo region = attachInfo.getRegionInfo();
        if (region.getType().startsWith(RegionEnum.SEA.type()) || region.getType().startsWith(RegionEnum.BANK.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(WATER)));
            log.info("水之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(WATER)));
        }
        return attributesPower;
    }
}
