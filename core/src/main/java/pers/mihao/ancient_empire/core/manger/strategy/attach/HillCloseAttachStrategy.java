package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;

public class HillCloseAttachStrategy extends AttachStrategy {

    private  String HILL = "unitMes.hillClose.buff";



    /**
     * 获取山头的攻击
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
        RegionInfo region = attachInfo.getRegionInfo();
        if (region.getType().equals(RegionEnum.STONE.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(HILL)));
            log.info("山之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(HILL)));
        }
        return attributesPower;
    }
}
