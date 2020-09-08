package pers.mihao.ancient_empire.core.handel.end;

import com.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.constant.AbilityEnum;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

/**
 * 单位行动结束后的handle
 */
public class EndHandle {

    protected EndHandle() {
    }

    public static EndHandle endHandle = null;

    /**
     * 根据单位的 能力选择相应的能力处理器
     * @param abilityType
     * @return
     */
    public static EndHandle initActionHandle(String abilityType) {
        AbilityEnum type = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (type) {
            case PURIFY:
                return PurifyEndHandle.instance();
            case WEAKER:
                return WeakerEndHandle.instance();
            default:
                return null;
        }
    }

    public static EndHandle getDefaultHandle() {
        if (endHandle == null) {
            return new EndHandle();
        }
        return endHandle;
    }

    public RespEndResultDto getEndResult(RespEndResultDto respEndResultDto, UserRecord record, Unit unit){
        return respEndResultDto;
    }
}
