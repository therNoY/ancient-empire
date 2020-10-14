package pers.mihao.ancient_empire.core.manger.strategy.end;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;

/**
 * 单位行动结束后的handle
 */
public class EndStrategy {


    public static EndStrategy endStrategy = null;

    /**
     * 根据单位的 能力选择相应的能力处理器
     *
     * @param abilityType
     * @return
     */
    public static EndStrategy initActionHandle(String abilityType) {
        AbilityEnum type = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (type) {
            case PURIFY:
                return PurifyEndStrategy.instance();
            case WEAKER:
                return WeakerEndStrategy.instance();
            default:
                return null;
        }
    }

    public static EndStrategy getDefaultHandle() {
        if (endStrategy == null) {
            return new EndStrategy();
        }
        return endStrategy;
    }

    public RespEndResultDto getEndResult(RespEndResultDto respEndResultDto, UserRecord record, Unit unit) {
        return respEndResultDto;
    }
}
