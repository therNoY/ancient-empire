package com.mihao.ancient_empire.websocket.service;


import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.UnitEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.mongo_dto.SummonDto;
import com.mihao.ancient_empire.dto.ws_dto.*;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.LevelHelper;
import com.mihao.ancient_empire.util.MqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
    UnitLevelMesService levelMesService;
    @Autowired
    UnitMesService unitMesService;
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
        UnitLevelMes levelMes;
        UnitMes unitMes;
        Integer armyIndex = AppUtil.getCurrentArmyIndex(record);
        Unit unit = record.getArmyList().get(armyIndex).getUnits().get(summonDto.getIndex());
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType()); // 攻击者能力
        RespSummonResult respSummonResult = new RespSummonResult();
        // 1.1 判断是否升级
        respSummonResult.setEndExperience(summonExperience);
        levelHelper.handleLevel(respSummonResult, unit.getLevel());

        // 2. 判断是否有二次移动
        for (Ability ability : abilityList) {
            if (ability.getType().equals(AbilityEnum.ASSAULT.getType())) {
                // 是可以进行二次移动
                levelMes = levelMesService.getUnitLevelMes(unit.getType(), unit.getLevel());
                unitMes = unitMesService.getByType(unit.getType());
                SecondMoveDto secondMoveDto = new SecondMoveDto();
                int lastSpeed = getLastSpeed(summonDto.getPath(), levelMes.getSpeed());
                if (lastSpeed > 0) {
                    List<Position> positions = moveAreaService.getSecondMoveArea(record, unit, unitMes, lastSpeed);
                    secondMoveDto.setSecondMove(true);
                    secondMoveDto.setMoveArea(positions);
                    respSummonResult.setSecondMove(secondMoveDto);
                }
            }
        }

        if(respSummonResult.getLeaveUp() != null && respSummonResult.getLeaveUp()) {
            unit.setLevel(unit.getLevel() + 1);
        }

        Unit newUnit = new Unit(UnitEnum.BONE.getType(), summonDto.getTomb().getRow(), summonDto.getTomb().getColumn());
        newUnit.setLevel(unit.getLevel());

        respSummonResult.setArmyIndex(armyIndex);
        respSummonResult.setBone(newUnit);

        // 3.mq更新mongo 坟墓减少 军队增加1 坟墓减少1
        mqHelper.sendMongoCdr(MqMethodEnum.ACTION_SUMMON, new SummonDto(uuid, summonDto.getIndex(),respSummonResult, summonDto.getTomb()));
        return respSummonResult;
    }

    /**
     * 获取 单位的剩余移动力
     */
    private int getLastSpeed(List<PathPosition> path, int speed) {
        int sum = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            PathPosition p1 = path.get(i);
            PathPosition p2 = path.get(i + 1);
            sum = sum + getPathPositionLength(p1, p2);
        }
        return speed - sum;
    }

    /**
     * 获取两点消耗的移动力 本来应该根据能力算出不同的能力消耗
     * @param p1
     * @param p2
     * @return
     */
    private int getPathPositionLength(PathPosition p1, PathPosition p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

}
