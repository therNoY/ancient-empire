package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.move_area.MoveAreaHandle;
import com.mihao.ancient_empire.handle.move_path.MovePathHandle;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取和单位移动有关的
 */
@Service
public class WsMoveAreaService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    AbilityService abilityService;
    @Autowired
    UnitLevelMesService unitLevelMesService;
    @Autowired
    WsActionService actionService;


    /**
     * 获取单位的移动范围
     *
     * @param unitIndex
     * @return
     */
    public Object getMoveArea(String uuid, ReqUnitIndexDto unitIndex) {
        // 1.获取record
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        Army cArmy = AppUtil.getArmyByIndex(userRecord, unitIndex.getArmyIndex());
        // TODO 领主占领城镇
        String color = cArmy.getColor();
        Unit cUnit = cArmy.getUnits().get(unitIndex.getIndex());
        UnitMes cUnitMes = unitMesService.getByType(cUnit.getType());
        List<Ability> abilityList = abilityService.getUnitAbilityList(cUnitMes.getId());
        // 2. 找到单位的所有能力 从能力中找自动范围
        List<Position> positions = new ArrayList<>();
        // 判断如果是
        if (abilityList.contains(new Ability(AbilityEnum.CASTLE_GET.getType()))) {
            // 判断移动单位是否有领主属性 如果有判断是否站在所属城堡
            BaseSquare region = AppUtil.getRegionByPosition(userRecord, cUnit);
            if (region.getType().equals(RegionEnum.CASTLE.getType()) && region.getColor().equals(color)) {
                Map<String, Object> map = actionService.getActions(uuid, new ReqMoveDto(unitIndex.getArmyIndex(), AppUtil.getPosition(cUnit), AppUtil.getPosition(cUnit)), true);
                return map;
            }
        }

        UnitLevelMes levelMes = unitLevelMesService.getUnitLevelMes(cUnit.getType(), cUnit.getLevel());

        for (Ability ab : abilityList) {
            MoveAreaHandle moveAreaHandle = MoveAreaHandle.initActionHandle(ab.getType());
            if (moveAreaHandle != null) {
                List<Position> abMove = moveAreaHandle.getMoveArea(userRecord, unitIndex, levelMes);
                if (abMove != null) {
                    positions.addAll(abMove);
                }
            }
        }
        // 如果单位没有有效能力
        if (positions.size() == 0) {
            MoveAreaHandle defaultHandle = MoveAreaHandle.getDefaultHandle();
            positions.addAll(defaultHandle.getMoveArea(userRecord, unitIndex, levelMes));
        }
        // 去重
        List<Position> moveArea = new ArrayList<>();
        for (Position p : positions) {
            if (!moveArea.contains(p)) {
                moveArea.add(new Position(p));
            }
        }
        return moveArea;
    }


    /**
     * 获取移动路径
     *
     * @param moveDto
     * @return
     */
    public List<PathPosition> getMovePath(ReqMoveDto moveDto) {
        List<PathPosition> pathPositions = MovePathHandle.getMovePath(moveDto);
        return pathPositions;
    }

    /**
     * 获取单位二次移动的移动范围
     * @return
     */
    public List<Position> getSecondMoveArea(UserRecord userRecord, Unit cUnit, UnitMes cUnitMes, int lastSpeed){

        Army army = null;
        for (Army a : userRecord.getArmyList()){
            if (a.getColor().equals(userRecord.getCurrColor())) {
                army = a;
                break;
            }
        }
        if (army == null) {
            log.error("当前单位颜色记录错误");
            return null;
        }

        List<Ability> abilityList = abilityService.getUnitAbilityList(cUnitMes.getId());
        // 2. 找到单位的所有能力 从能力中找自动范围
        List<Position> positions = new ArrayList<>();

        UnitLevelMes levelMes = unitLevelMesService.getUnitLevelMes(cUnit.getType(), cUnit.getLevel());
        levelMes.setSpeed(lastSpeed);

        for (Ability ab : abilityList) {
            MoveAreaHandle moveAreaHandle = MoveAreaHandle.initActionHandle(ab.getType());
            if (moveAreaHandle != null) {
                List<Position> abMove = moveAreaHandle.getMoveArea(userRecord, army, cUnit, levelMes);
                if (abMove != null) {
                    positions.addAll(abMove);
                }
            }
        }
        // 如果单位没有有效能力
        if (positions.size() == 0) {
            MoveAreaHandle defaultHandle = MoveAreaHandle.getDefaultHandle();
            positions.addAll(defaultHandle.getMoveArea(userRecord, army, cUnit, levelMes));
        }
        // 去重
        List<Position> moveArea = new ArrayList<>();
        for (Position p : positions) {
            if (!moveArea.contains(p)) {
                moveArea.add(new Position(p));
            }
        }
        return moveArea;
    }
}