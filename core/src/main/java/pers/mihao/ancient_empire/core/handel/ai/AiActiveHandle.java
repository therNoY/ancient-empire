package pers.mihao.ancient_empire.core.handel.ai;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.core.dto.ai.ActiveResult;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;
import pers.mihao.ancient_empire.core.handel.end.EndHandle;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

/**
 * 具体任务的处理类 根据不同的类型返回不同的处理类 是简单工厂模式
 */
public abstract class AiActiveHandle {

    protected static UserRecordService userRecordService = ApplicationContextHolder.getBean(UserRecordService.class);
    protected static AbilityService abilityService = ApplicationContextHolder.getBean(AbilityService.class);
    protected static UnitMesService unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);

    public static AiActiveHandle getInstance(UserRecord record, AiActiveEnum activeEnum) {
        switch (activeEnum) {
            case SELECT_UNIT:
                return GameCoreManger.getInstance(record).getSelectUnitHandle();
            case MOVE_UNIT:
                return GameCoreManger.getInstance(record).getMoveUnitHandle();
            default:
                return null;
        }
    }

    public abstract ActiveResult getActiveResult(UserRecord record);


    public static RespEndResultDto getEndDto(UserRecord record, Unit unit) {
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
        RespEndResultDto respEndResultDto = new RespEndResultDto();
        for (Ability ability : abilityList) {
            EndHandle endHandle = EndHandle.initActionHandle(ability.getType());
            if (endHandle != null) {
                endHandle.getEndResult(respEndResultDto, record, unit);
            }
        }

        return respEndResultDto;
    }
}
