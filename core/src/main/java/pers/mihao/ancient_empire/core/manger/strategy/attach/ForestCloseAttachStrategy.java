package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;

public class ForestCloseAttachStrategy extends AttachStrategy {

    private  String FOREST = "unitMes.forestClose.buff";



    /**
     * 获取森林的攻击
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
        if (region.getType().equals(RegionEnum.FOREST.type()) || region.getType().equals(RegionEnum.GROVE.type())) {
            attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(FOREST)));
            log.info("森林之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(FOREST)));

        }
        return attributesPower;
    }
}
