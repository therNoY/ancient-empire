package com.mihao.ancient_empire.ai.handle;

import com.mihao.ancient_empire.ai.RobotManger;
import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.ActiveResult;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.end.EndHandle;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
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
                return RobotManger.getInstance(record).getSelectUnitHandle();
            case MOVE_UNIT:
                return RobotManger.getInstance(record).getMoveUnitHandle();
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
