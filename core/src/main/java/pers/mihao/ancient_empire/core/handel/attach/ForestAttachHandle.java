package pers.mihao.ancient_empire.core.handel.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

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
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        BaseSquare region = AppUtil.getRegionByPosition(record, unit);
        if (region.getType().equals(RegionEnum.FOREST.type()) || region.getType().equals(RegionEnum.GROVE.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(FOREST)));
                log.info("森林之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(FOREST)));
            } else {
                int attach = AppUtil.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(FOREST)));
            }
        }
        return attributesPower;
    }
}
