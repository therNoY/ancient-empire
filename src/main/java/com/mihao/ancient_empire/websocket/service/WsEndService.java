package com.mihao.ancient_empire.websocket.service;


import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.end.EndHandle;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitAbilityService;
import com.mihao.ancient_empire.service.UserRecordService;
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
