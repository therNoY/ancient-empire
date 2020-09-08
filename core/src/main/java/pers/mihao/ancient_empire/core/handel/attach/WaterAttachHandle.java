package pers.mihao.ancient_empire.core.handel.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

public class WaterAttachHandle extends AttachHandle {

    private static String WATER = "unit.waterClose.buff";
    private static WaterAttachHandle waterAttachHandle = null;

    private WaterAttachHandle() {
    }

    public static WaterAttachHandle instance() {
        if (waterAttachHandle == null) {
            waterAttachHandle = new WaterAttachHandle();
        }
        return waterAttachHandle;
    }

    /**
     * 获取海洋的攻击
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
        if (region.getType().startsWith(RegionEnum.SEA.type()) || region.getType().startsWith(RegionEnum.BANK.type())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(WATER)));
                log.info("水之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(WATER)));
            } else {
                int attach = AppUtil.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(WATER)));
            }
        }
        return attributesPower;
    }
}
