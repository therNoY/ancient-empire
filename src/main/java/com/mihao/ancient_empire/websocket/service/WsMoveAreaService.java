package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.*;
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
        Army cArmy = null;
        boolean getLoadAction = true;
        if (unitIndex.getArmyIndex() != null) {
            cArmy = AppUtil.getArmyByIndex(userRecord, unitIndex.getArmyIndex());
        }else {
            getLoadAction = false;
            List<Army> armyList = userRecord.getArmyList();
            for (int i = 0; i < armyList.size(); i++)  {
                Army army = armyList.get(i);
                if (userRecord.getCurrColor().equals(army.getColor())) {
                    cArmy = army;
                    unitIndex.setArmyIndex(i);
                    break;
                }
            }
        }
        // TODO 领主占领城镇
        String color = cArmy.getColor();
        Unit cUnit = cArmy.getUnits().get(unitIndex.getIndex());
        UnitMes cUnitMes = unitMesService.getByType(cUnit.getType());
        List<Ability> abilityList = abilityService.getUnitAbilityList(cUnitMes.getId());
        // 2. 找到单位的所有能力 从能力中找自动范围
        List<Position> positions = new ArrayList<>();
        // 判断如果是 获取action
        if (getLoadAction && abilityList.contains(new Ability(AbilityEnum.CASTLE_GET.getType()))) {
            // 判断移动单位是否有领主属性 如果有判断是否站在所属城堡
            BaseSquare region = AppUtil.getRegionByPosition(userRecord, cUnit);
            if (region.getType().equals(RegionEnum.CASTLE.getType()) && region.getColor().equals(color)) {
                Map<String, Object> map = actionService.getActions(uuid, new ReqMoveDto(unitIndex.getIndex(), AppUtil.getPosition(cUnit), AppUtil.getPosition(cUnit)), true);
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

    public SecondMoveDto getSecondMove(Unit unit, UserRecord record, ReqSecondMoveDto reqSecondMoveDto) {
        // 2. 判断是否有二次移动
        SecondMoveDto secondMoveDto = null;
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType()); // 攻击者能力
        UnitLevelMes levelMes;
        UnitMes unitMes;
        for (Ability ability : abilityList) {
            if (ability.getType().equals(AbilityEnum.ASSAULT.getType())) {
                // 是可以进行二次移动
                levelMes = unitLevelMesService.getUnitLevelMes(unit.getType(), unit.getLevel());
                unitMes = unitMesService.getByType(unit.getType());
                secondMoveDto = new SecondMoveDto();
                int lastSpeed = getLastSpeed(reqSecondMoveDto.getPath(), levelMes.getSpeed());
                if (lastSpeed > 0) {
                    List<Position> positions = getSecondMoveArea(record, unit, unitMes, lastSpeed);
                    secondMoveDto.setSecondMove(true);
                    secondMoveDto.setMoveArea(positions);
                    break;
                }
            }
        }
        return secondMoveDto;
    }


    /**
     * 获取 单位的剩余移动力
     */
    private int getLastSpeed(List<PathPosition> path, int speed) {
        int sum = 0;
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                PathPosition p1 = path.get(i);
                PathPosition p2 = path.get(i + 1);
                sum = sum + getPathPositionLength(p1, p2);
            }
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
