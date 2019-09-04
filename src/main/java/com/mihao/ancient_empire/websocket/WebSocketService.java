package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.*;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.dto.ws_dto.RespAction;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.action.ActionHandle;
import com.mihao.ancient_empire.handle.move_area.MoveAreaHandle;
import com.mihao.ancient_empire.handle.move_path.MovePathHandle;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebSocketService implements ApplicationContextAware {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    AbilityService abilityService;

    private ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

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
                Map<String, Object> map = getActions(uuid, new ReqMoveDto(unitIndex.getArmyIndex(), AppUtil.getPosition(cUnit), AppUtil.getPosition(cUnit)), true);
                return map;
            }
        }

        for (Ability ab : abilityList) {
            MoveAreaHandle moveAreaHandle = MoveAreaHandle.initActionHandle(ab.getType(), userRecord, unitIndex, ac);
            if (moveAreaHandle != null) {
                List<Position> abMove = moveAreaHandle.getMovePosition();
                if (abMove != null) {
                    positions.addAll(abMove);
                }
            }
        }
        // 如果单位没有有效能力
        if (positions.size() == 0) {
            MoveAreaHandle defaultHandle = MoveAreaHandle.getDefaultHandle(userRecord, unitIndex, ac);
            positions.addAll(defaultHandle.getMovePosition());
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
     * 获取单位移动后的行动选项
     *
     * @param moveDto
     * @return
     */
    public Map<String, Object> getActions(String uuid, ReqMoveDto moveDto, boolean isLoad) {
        Map<String, Object> actionMap = new HashMap<>();
        // 1.获取record 获取当前单位信息 主要获取
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        String color = userRecord.getCurrColor();
        Army cArmy = AppUtil.getArmyByColor(userRecord, color);
        Unit cUnit = cArmy.getUnits().get(moveDto.getCurrentUnitIndex());
        UnitMes cUnitMes = unitMesService.getByType(cUnit.getType());
        // 2. 获取单位的攻击范围
        List<Position> positions = getAttachArea(cUnitMes, moveDto.getAimPoint(), userRecord);
        // 3 .获取当前单位的所有能力
        List<Ability> abilityList = abilityService.getUnitAbilityList(cUnitMes.getId());
        // 4. 获取单位的所有能力
        Set<String> actionSet = new HashSet<>();
        // 获取默认处理
        ActionHandle defaultHandle = ActionHandle.getDefaultHandle();
        List<String> defaultActions = defaultHandle.getAction(positions, userRecord, color, moveDto.getCurrentUnitIndex(), moveDto.getAimPoint());
        actionSet.addAll(defaultActions);
        // 获取特殊处理
        for (Ability ab : abilityList) {
            // 根据能力获取所有的行为
            ActionHandle actionHandle = ActionHandle.initActionHandle(ab.getType());
            if (actionHandle != null) {
                List<String> actions = actionHandle.getAction(positions, userRecord, color, moveDto.getCurrentUnitIndex(), moveDto.getAimPoint());
                actionSet.addAll(actions);
            }
        }
        if (isLoad) {
            actionSet.add(ActionEnum.BUY.getType());
            actionSet.add(ActionEnum.MOVE.getType());
        }
        actionMap.put("cAction", AppUtil.addActionAim(new ArrayList<>(actionSet), moveDto.getAimPoint()));
        // 5. 渲染不同的action 不同的位置显示
        List<RespAction> respActions = AppUtil.addActionPosition(new ArrayList<>(actionSet), moveDto.getAimPoint());
        actionMap.put("mAction", respActions);

        return actionMap;
    }

    /**
     * 获取单位的 攻击范围
     *
     * @param unitMes
     * @param aimP
     * @param userRecord
     * @return
     */
    public List<Position> getAttachArea(UnitMes unitMes, Position aimP, UserRecord userRecord) {
        Integer maxRange = unitMes.getMaxAttachRange();
        List<Position> maxAttach = new ArrayList<>();
        for (int i = aimP.getRow() - maxRange; i < aimP.getRow() + maxRange + 1; i++) {
            for (int j = aimP.getColumn() - maxRange; j < aimP.getColumn() + maxRange + 1; j++) {
                if (getPositionLength(i, j, aimP.getRow(), aimP.getColumn()) <= maxRange && getPositionLength(i, j, aimP.getRow(), aimP.getColumn()) > 0) {
                    maxAttach.add(new Position(i, j));
                }
            }
        }
        Integer minRange = unitMes.getMinAttachRange();
        List<Position> notAttach = null;
        if (minRange != 1) {
            // 获取无法攻击到的点
            notAttach = new ArrayList<>();
            for (int i = aimP.getRow() - minRange; i < aimP.getRow() + minRange; i++) {
                for (int j = aimP.getColumn() - minRange; j < aimP.getColumn() + minRange; j++) {
                    if (getPositionLength(i, j, aimP.getRow(), aimP.getColumn()) <= minRange) {
                        notAttach.add(new Position(i, j));
                    }
                }
            }

        }

        int row = userRecord.getInitMap().getRow();
        int column = userRecord.getInitMap().getColumn();
        // 过滤符合条件的点
        List<Position> finalNotAttach = notAttach;
        return maxAttach.stream().filter(position -> {
            // 在地图范围内
            if (position.getRow() <= row && position.getColumn() <= column) {
                // 不在不可攻击范围内
                if (finalNotAttach == null || !finalNotAttach.contains(position)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private int getPositionLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }
}
