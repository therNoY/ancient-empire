package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.AttributesPower;

import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

public class PoisoningAttachStrategy extends AttachStrategy {

    private  String POISONING = "unitMes.poisoning.buff";


    /**
     * 获取投毒者攻击
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
        if (!StringUtil.isEmpty(beAttachUnitInfo.getStatus())) {
            if (beAttachUnitInfo.getStatus().equals(StateEnum.EXCITED.type())) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(POISONING)));
                log.info("投毒者 获取攻击加成{}", Integer.valueOf(AppConfig.get(POISONING)));
            }
        }
        return attributesPower;
    }
}
