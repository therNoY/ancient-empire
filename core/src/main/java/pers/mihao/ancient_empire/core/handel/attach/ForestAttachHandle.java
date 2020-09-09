package pers.mihao.ancient_empire.core.handel.attach;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

public class ForestAttachHandle extends AttachHandle {

    private static String FOREST = "unit.forestClose.buff";

    private static ForestAttachHandle forestAttachHandle = null;

    private ForestAttachHandle() {
    }

    public static ForestAttachHandle instance() {
        if (forestAttachHandle == null) {
            forestAttachHandle = new ForestAttachHandle();
        }
        return forestAttachHandle;
    }

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
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit,
        AttributesPower attributesPower) {
        BaseSquare region = GameCoreHelper.getRegionByPosition(record, unit);
        if (region.getType().equals(RegionEnum.FOREST.type()) || region.getType().equals(RegionEnum.GROVE.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(FOREST)));
                log.info("森林之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(FOREST)));
            } else {
                int attach = GameCoreHelper.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(FOREST)));
            }
        }
        return attributesPower;
    }
}
