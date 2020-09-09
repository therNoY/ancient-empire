package pers.mihao.ancient_empire.core.websocket.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.SummonDto;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.LevelHelper;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.core.dto.ReqSummonDto;
import pers.mihao.ancient_empire.core.dto.RespSummonResult;
import pers.mihao.ancient_empire.core.dto.SecondMoveDto;

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
