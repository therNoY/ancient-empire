package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
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
        if (region.getType().startsWith(RegionEnum.SEA.getType()) || region.getType().startsWith(RegionEnum.BANK.getType())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(WATER)));
            } else {
                int attach = AppUtil.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(WATER)));
            }
        }
        return attributesPower;
    }
}
