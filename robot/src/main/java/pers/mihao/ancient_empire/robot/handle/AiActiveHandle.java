package pers.mihao.ancient_empire.robot.handle;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;
import pers.mihao.ancient_empire.robot.dto.ActiveResult;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.common.bo.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.Ability;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.core.handel.end.EndHandle;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.auth.service.AbilityService;
import pers.mihao.ancient_empire.auth.service.UnitMesService;
import pers.mihao.ancient_empire.auth.service.UserRecordService;
import com.mihao.ancient_empire.util.ApplicationContextHolder;

import java.util.List;

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
