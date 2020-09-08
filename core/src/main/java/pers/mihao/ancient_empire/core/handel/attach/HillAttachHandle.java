package pers.mihao.ancient_empire.core.handel.attach;

import com.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.RegionEnum;
import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.AttributesPower;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
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
        if (region.getType().equals(RegionEnum.STONE.type())) {
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
