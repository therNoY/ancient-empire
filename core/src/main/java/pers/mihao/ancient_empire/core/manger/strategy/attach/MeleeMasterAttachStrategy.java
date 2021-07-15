package pers.mihao.ancient_empire.core.manger.strategy.attach;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;


/**
 * @author mihao
 */
public class MeleeMasterAttachStrategy extends AttachStrategy {

    private String MELEE_MASTER = "unitMes.meleeMaster.buff";


    /**
     * 获取近战大师攻击
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
        if (isReach(attachInfo, beAttachUnitInfo)) {
            Float addition = Float.valueOf(AppConfig.get(MELEE_MASTER));
            if (attributesPower.getAddition() != null) {
                if (addition > attributesPower.getAddition()) {
                    attributesPower.setAddition(addition);
                    log.info("近战大师 获取攻击加成{}倍", Integer.valueOf(AppConfig.get(MELEE_MASTER)));
                }
            } else {
                attributesPower.setAddition(addition);
            }
        }
        return attributesPower;
    }

    /**
     * 判断两点是否可达
     *
     * @param currP
     * @param aimP
     * @return
     */
    private boolean isReach(Unit currP, Unit aimP) {
        if (Math.abs(currP.getRow() - aimP.getRow()) + Math.abs(currP.getColumn() - aimP.getColumn()) == 1) {
            return true;
        }
        return false;
    }
}
