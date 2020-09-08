package pers.mihao.ancient_empire.core.websocket.service;


import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.Ability;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.core.handel.end.EndHandle;
import pers.mihao.ancient_empire.auth.service.AbilityService;
import pers.mihao.ancient_empire.auth.service.UnitAbilityService;
import pers.mihao.ancient_empire.auth.service.UserRecordService;
import com.mihao.ancient_empire.util.MqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service 结束时的 处理
 */
@Service
public class WsEndService {


    @Autowired
    UserRecordService userRecordService;
    @Autowired
    AbilityService abilityService;
    @Autowired
    MqHelper mqHelper;

    public RespEndResultDto getEndResult(String uuid, Unit unit) {
        UserRecord record = userRecordService.getRecordById(uuid);
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());

        RespEndResultDto respEndResultDto = new RespEndResultDto();
        for (Ability ability : abilityList) {
            EndHandle endHandle = EndHandle.initActionHandle(ability.getType());
            if (endHandle != null) {
                endHandle.getEndResult(respEndResultDto, record, unit);
            }
        }

        // 同步mongo
        respEndResultDto.setUuid(uuid);
        respEndResultDto.setUnitId(unit.getId());
        respEndResultDto.setRow(unit.getRow());
        respEndResultDto.setColumn(unit.getColumn());
        mqHelper.sendMongoCdr(MqMethodEnum.ACTION_END, respEndResultDto);
        return respEndResultDto;
    }
}
