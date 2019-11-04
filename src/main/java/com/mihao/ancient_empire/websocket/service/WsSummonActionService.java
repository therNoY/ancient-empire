package com.mihao.ancient_empire.websocket.service;


import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.UnitEnum;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.mongo_dto.SummonDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqSummonDto;
import com.mihao.ancient_empire.dto.ws_dto.RespSummonResult;
import com.mihao.ancient_empire.dto.ws_dto.SecondMoveDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.LevelHelper;
import com.mihao.ancient_empire.util.MqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WsSummonActionService {

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    AbilityService abilityService;
    @Value("${experience.summon}")
    Integer summonExperience;
    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    LevelHelper levelHelper;
    @Autowired
    MqHelper mqHelper;

    /**
     * 获取召唤结果
     * @param uuid
     * @param summonDto
     * @return
     */
    public RespSummonResult getSummonResult(String uuid, ReqSummonDto summonDto) {
        UserRecord record = userRecordService.getRecordById(uuid);
        Integer armyIndex = AppUtil.getCurrentArmyIndex(record);
        Unit unit = record.getArmyList().get(armyIndex).getUnits().get(summonDto.getIndex());
        RespSummonResult respSummonResult = new RespSummonResult();
        // 1.1 判断是否升级
        respSummonResult.setEndExperience(summonExperience);
        levelHelper.handleLevel(respSummonResult, unit.getLevel());

        // 2. 判断是否二次移动
        SecondMoveDto secondMoveDto = moveAreaService.getSecondMove(unit, record, summonDto);
        respSummonResult.setSecondMove(secondMoveDto);

        // 判断是否升级
        if(respSummonResult.getLeaveUp() != null && respSummonResult.getLeaveUp()) {
            unit.setLevel(unit.getLevel() + 1);
        }
        Unit newUnit = new Unit(UnitEnum.BONE.type(), summonDto.getTomb().getRow(), summonDto.getTomb().getColumn());
        newUnit.setLevel(unit.getLevel());

        respSummonResult.setArmyIndex(armyIndex);
        respSummonResult.setBone(newUnit);

        // 3.mq更新mongo 坟墓减少 军队增加1 坟墓减少1
        mqHelper.sendMongoCdr(MqMethodEnum.ACTION_SUMMON, new SummonDto(uuid, summonDto.getIndex(),respSummonResult, summonDto.getTomb(), newUnit));
        return respSummonResult;
    }
}
