package com.mihao.ancient_empire.handle.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;

public class DestroyerAttachHandle extends AttachHandle {

    private static String DESTROYER = "unit.destroyer.buff";

    private static DestroyerAttachHandle destroyerAttachHandle = null;

    private DestroyerAttachHandle() {
    }

    public static DestroyerAttachHandle instance() {
        if (destroyerAttachHandle == null) {
            destroyerAttachHandle = new DestroyerAttachHandle();
        }
        return destroyerAttachHandle;
    }


    /**
     * 破化者的攻击加成
     *
     * @param record
     * @param unit
     * @param levelMes
     * @param beAttachUnit
     * @param attributesPower
     * @return
     */
    @Override
    public AttributesPower getAttachPower(UserRecord record, Unit unit, UnitLevelMes levelMes, Unit beAttachUnit, AttributesPower attributesPower) {
        BaseSquare region = AppUtil.getRegionByPosition(record, beAttachUnit);
        if (region.getType().equals(RegionEnum.TOWN.getType())) {

            if (attributesPower.getNum() != null) {
                attributesPower.setNum(attributesPower.getNum() + Integer.valueOf(AppConfig.get(DESTROYER)));
                log.info("破化者获取攻击加成{}", Integer.valueOf(AppConfig.get(DESTROYER)));
            } else {
                int attach = AppUtil.getAttachNum(levelMes);
                attributesPower.setNum(attach + Integer.valueOf(AppConfig.get(DESTROYER)));
            }
        }
        return attributesPower;
    }
}
