package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.ReqAttachAreaDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.RespAction;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.action.ActionHandle;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取和单位行动有关的数据
 */
@Service
public class WsActionService {


    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    UnitLevelMesService unitLevelMesService;
    @Autowired
    AbilityService abilityService;

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
        Integer camp = userRecord.getCurrCamp();
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
        List<String> defaultActions = defaultHandle.getAction(positions, userRecord, camp, moveDto.getCurrentUnitIndex(), moveDto.getAimPoint());
        actionSet.addAll(defaultActions);
        // 获取特殊处理
        for (Ability ab : abilityList) {
            // 根据能力获取所有的行为
            ActionHandle actionHandle = ActionHandle.initActionHandle(ab.getType());
            if (actionHandle != null) {
                List<String> actions = actionHandle.getAction(positions, userRecord, camp, moveDto.getCurrentUnitIndex(), moveDto.getAimPoint());
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
     * 获取单位的 攻击范围 外部接口
     *
     * @return
     */
    public List<Position> getAttachArea(String uuid, ReqAttachAreaDto moveDto) {
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        Unit unit = AppUtil.getUnitByIndex(userRecord, moveDto.getIndex());
        return getAttachArea(unitMesService.getByType(unit.getType()), moveDto.getPosition(), userRecord);
    }

    /**
     * 获取单位的 攻击范围
     *
     * @param unitMes
     * @param aimP
     * @param userRecord
     * @return
     */
    private List<Position> getAttachArea(UnitMes unitMes, Position aimP, UserRecord userRecord) {
        Integer maxRange = unitMes.getMaxAttachRange();
        List<Position> maxAttach = new ArrayList<>();
        int minI = Math.max(aimP.getRow() - maxRange, 1);
        int maxI = Math.min(aimP.getRow() + maxRange + 1, userRecord.getInitMap().getRow() + 1);
        int minJ = Math.max(aimP.getColumn() - maxRange, 1);
        int maxJ = Math.min(aimP.getColumn() + maxRange + 1, userRecord.getInitMap().getRow() + 1);
        for (int i = minI; i < maxI; i++) {
            for (int j = minJ; j < maxJ; j++) {
                if (getPositionLength(i, j, aimP.getRow(), aimP.getColumn()) <= maxRange && getPositionLength(i, j, aimP.getRow(), aimP.getColumn()) > 0) {
                    maxAttach.add(new Position(i, j));
                }
            }
        }
        Integer minRange = unitMes.getMinAttachRange();
        List<Position> notAttach = null;
        if (minRange != 1) {
            // 获取无法攻击到的点
            minRange = minRange - 1;
            notAttach = new ArrayList<>();
            minI = Math.max(aimP.getRow() - minRange, 0);
            maxI = Math.min(aimP.getRow() + minRange + 1, userRecord.getInitMap().getRow());
            minJ = Math.max(aimP.getColumn() - minRange, 0);
            maxJ = Math.min(aimP.getColumn() + minRange + 1, userRecord.getInitMap().getRow());
            for (int i = minI; i < maxI; i++) {
                for (int j = minJ; j < maxJ; j++) {
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

    /**
     * 获取两点的距离
     * @param row
     * @param column
     * @param row2
     * @param column2
     * @return
     */
    private int getPositionLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

}