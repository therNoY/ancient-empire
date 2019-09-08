package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

public class HillAttachHandle extends AttachHandle {

    private static String HILL = "unit.hillClose.buff";

    private static HillAttachHandle hillAttachHandle = null;

    private HillAttachHandle() {
    }

    public static HillAttachHandle instance() {
        if (hillAttachHandle == null) {
            hillAttachHandle = new HillAttachHandle();
        }
        return hillAttachHandle;
    }

    /**
     * 获取山头的攻击
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
        if (region.getType().equals(RegionEnum.STONE.getType())) {
            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(HILL)));
                log.info("山之子 获取攻击加成{}", Integer.valueOf(AppConfig.get(HILL)));
            } else {
                int attach = AppUtil.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(HILL)));
            }
        }
        return attributesPower;
    }
}
