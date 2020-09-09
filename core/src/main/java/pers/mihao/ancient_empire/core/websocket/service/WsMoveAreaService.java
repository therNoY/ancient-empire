package pers.mihao.ancient_empire.core.websocket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.dto.ReqMoveDto;
import pers.mihao.ancient_empire.core.dto.ReqSecondMoveDto;
import pers.mihao.ancient_empire.core.dto.ReqUnitIndexDto;
import pers.mihao.ancient_empire.core.dto.SecondMoveDto;
import pers.mihao.ancient_empire.core.handel.move_area.MoveAreaHandle;
import pers.mihao.ancient_empire.core.handel.move_path.MovePathHandle;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

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


    public Object getMoveArea(String uuid, ReqUnitIndexDto unitIndex) {
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        return getMoveArea(userRecord, unitIndex, null, true);
    }

    /**
     * 获取单位的移动范围
     *
     * @param userRecord   record
     * @param unitIndex
     * @param unit         要移动的单位
     * @param considerLoad 是否考虑领主移动 当人机的时候不会出现
     * @return
     */
    public Object getMoveArea(UserRecord userRecord, ReqUnitIndexDto unitIndex, Unit unit, boolean considerLoad) {
        // 1.获取record
        Army cArmy = null;
        boolean getLoadAction = true;

        if (unitIndex.getArmyIndex() != null) {
            cArmy = AppUtil.getArmyByIndex(userRecord, unitIndex.getArmyIndex());
        } else {
            getLoadAction = false;
            List<Army> armyList = userRecord.getArmyList();
            for (int i = 0; i < armyList.size(); i++) {
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
        Unit cUnit = (unit == null ? cArmy.getUnits().get(unitIndex.getIndex()) : unit);
        UnitMes cUnitMes = unitMesService.getByType(cUnit.getType());
        List<Ability> abilityList = abilityService.getUnitAbilityList(cUnitMes.getId());
        // 2. 找到单位的所有能力 从能力中找自动范围
        List<Position> positions = new ArrayList<>();
        // 判断如果是 获取action
        if (considerLoad && getLoadAction && abilityList.contains(AbilityEnum.CASTLE_GET.ability())) {
            // 判断移动单位是否有领主属性 如果有判断是否站在所属城堡
            BaseSquare region = GameCoreHelper.getRegionByPosition(userRecord, cUnit);
            if (region.getType().equals(RegionEnum.CASTLE.type()) && region.getColor().equals(color)) {
                Map<String, Object> map = actionService.getActions(userRecord, new ReqMoveDto(unitIndex.getIndex(), AppUtil.getPosition(cUnit), AppUtil.getPosition(cUnit)), true);
                return map;
            }
        }

        UnitLevelMes levelMes = unitLevelMesService.getUnitLevelMes(cUnit.getType(), cUnit.getLevel());

        for (Ability ab : abilityList) {
            MoveAreaHandle moveAreaHandle = MoveAreaHandle.initActionHandle(ab.getType());
            if (moveAreaHandle != null) {
                List<Position> abMove = moveAreaHandle.getMoveArea(userRecord, cUnit, levelMes);
                if (abMove != null) {
                    positions.addAll(abMove);
                }
            }
        }
        // 如果单位没有有效能力
        if (positions.size() == 0) {
            MoveAreaHandle defaultHandle = MoveAreaHandle.getDefaultHandle();
            positions.addAll(defaultHandle.getMoveArea(userRecord, cUnit, levelMes));
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
     *
     * @return
     */
    public List<Position> getSecondMoveArea(UserRecord userRecord, Unit cUnit, UnitMes cUnitMes, int lastSpeed) {

        Army army = AppUtil.getCurrentArmy(userRecord);

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
        // 如果单位没有有效能力 就设置成默认的
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
        return getSecondMove(unit, record, reqSecondMoveDto.getPath());
    }

    public SecondMoveDto getSecondMove(Unit unit, UserRecord record, List<PathPosition> path) {
        // 2. 判断是否有二次移动
        SecondMoveDto secondMoveDto = null;
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType()); // 攻击者能力
        UnitLevelMes levelMes;
        UnitMes unitMes;
        for (Ability ability : abilityList) {
            if (ability.getType().equals(AbilityEnum.ASSAULT.type())) {
                // 是可以进行二次移动
                levelMes = unitLevelMesService.getUnitLevelMes(unit.getType(), unit.getLevel());
                unitMes = unitMesService.getByType(unit.getType());
                secondMoveDto = new SecondMoveDto();
                int lastSpeed = getLastSpeed(path, levelMes.getSpeed());
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
     *
     * @param p1
     * @param p2
     * @return
     */
    private int getPathPositionLength(PathPosition p1, PathPosition p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

}
