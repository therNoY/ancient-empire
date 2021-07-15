package pers.mihao.ancient_empire.core.manger.strategy.defense;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @author mihao
 */
public class RemoteDefenseStrategy extends DefenseStrategy {

    private static String REMOTE_DEFENSE = "unitMes.remoteDefense.buff";

    /**
     * 获取远程防御的防御
     *
     * @param type
     * @param record
     * @param unit
     * @param levelMes
     * @param regionMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getDefensePower(GameContext gameContext, UnitInfo attachUnitInfo, UnitInfo beAttachUnitInfo,
        AttributesPower attributesPower) {
        if (!isReach(attachUnitInfo, beAttachUnitInfo)) {
            Float addition = Float.valueOf(AppConfig.get(REMOTE_DEFENSE));
            if (attributesPower.getAddition() != null) {
                if (addition > attributesPower.getAddition()) {
                    attributesPower.setAddition(addition);
                    log.info("远程防御 获取防御加成{} 倍", Integer.valueOf(AppConfig.get(REMOTE_DEFENSE)));
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
