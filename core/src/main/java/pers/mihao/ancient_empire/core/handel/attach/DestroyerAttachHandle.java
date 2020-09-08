package pers.mihao.ancient_empire.core.handel.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
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
        if (region.getType().equals(RegionEnum.TOWN.type())) {

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
