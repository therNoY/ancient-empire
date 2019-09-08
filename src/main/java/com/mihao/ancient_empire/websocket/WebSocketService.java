package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.*;
import com.mihao.ancient_empire.dto.ws_dto.*;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.action.ActionHandle;
import com.mihao.ancient_empire.handle.attach.AttachHandle;
import com.mihao.ancient_empire.handle.defense.DefenseHandle;
import com.mihao.ancient_empire.handle.move_area.MoveAreaHandle;
import com.mihao.ancient_empire.handle.move_path.MovePathHandle;
import com.mihao.ancient_empire.service.*;
import com.mihao.ancient_empire.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebSocketService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    UnitLevelMesService unitLevelMesService;
    @Autowired
    AbilityService abilityService;
    @Autowired
    RegionMesService regionMesService;
    @Value("${attach.experience}")
    Integer attachExperience;
    @Value("${counterattack.experience}")
    Integer counterattackExperience;
    @Value("${kill.experience}")
    Integer killExperience;
    @Value("${antikill.experience}")
    Integer antiKillExperience;
    @Value("${level0}")
    Integer level0;
    @Value("${level1}")
    Integer level1;
    @Value("${level2}")
    Integer level2;
    @Value("${level3}")
    Integer level3;

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
            MoveAreaHandle moveAreaHandle = MoveAreaHandle.initActionHandle(ab.getType());
            if (moveAreaHandle != null) {
                List<Position> abMove = moveAreaHandle.getMoveArea(userRecord, unitIndex);
                if (abMove != null) {
                    positions.addAll(abMove);
                }
            }
        }
        // 如果单位没有有效能力
        if (positions.size() == 0) {
            MoveAreaHandle defaultHandle = MoveAreaHandle.getDefaultHandle();
            positions.addAll(defaultHandle.getMoveArea(userRecord, unitIndex));
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

    /**
     * 获取攻击结果
     *
     * @param uuid         record 的uuid
     * @param reqAttachDto
     * @return
     */
    public RespAttachResultDto getAttachResult(String uuid, ReqAttachDto reqAttachDto) {
        // 最终返回的结果
        RespAttachResultDto resultDto = new RespAttachResultDto();

        // 1. 获取需要基本信息
        UserRecord record = userRecordService.getRecordById(uuid);
        Unit attachUnit = reqAttachDto.getAttachUnit();
        Unit beAttachUnit = reqAttachDto.getBeAttachUnit();
        UnitLevelMes attachUnitLevelMes = unitLevelMesService.getUnitLevelMes(attachUnit.getType(), attachUnit.getLevel());
        UnitLevelMes beAttachLevelUnitMes = unitLevelMesService.getUnitLevelMes(beAttachUnit.getType(), beAttachUnit.getLevel());
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(attachUnit.getType()); // 攻击者能力
        List<Ability> beAttachAbility = abilityService.getUnitAbilityListByType(beAttachUnit.getType()); // 被攻击者能力
        UnitMes attachUnitMes = unitMesService.getByType(attachUnit.getType());
        UnitMes beAttachUnitMes = unitMesService.getByType(beAttachUnit.getType());

        // 2. 获取主动攻击结果
        AttachResult attachResult = getOnceAttachResult(true, record, attachUnit, beAttachUnit,
                attachUnitMes, attachUnitLevelMes, beAttachLevelUnitMes, abilityList, beAttachAbility);
        resultDto.setAttachResult(attachResult);
        // 2.1 更新
        beAttachUnit.setLife(attachResult.getLastLife());
        if (!StringUtil.isEmpty(attachResult.getEndStatus())) {
            beAttachUnit.setStatus(attachResult.getEndStatus());
        }


        // 3.判断是否反击
        boolean counterattack = true;
        if (beAttachUnit.getStatus() != null && beAttachUnit.getStatus().equals(StateEnum.BLIND.getType())) {
            // 被致盲 无法反击
            counterattack = false;
        }
        if (counterattack && beAttachUnit.isDead()) {
            counterattack = false;
        }
        // 只有现在可以反击 判断反击的距离够不够
        if (counterattack && !AppUtil.isReach(attachUnit, beAttachUnit)) {
            // 不是挨着的 无法反击
            counterattack = false;
        }
        // 即便是挨着的 也有可能单位的攻击范围不够
        if (counterattack && beAttachLevelUnitMes.getMinAttack() == 1) {
            counterattack = false;
        }
        resultDto.setCounterattack(counterattack);


        // 4.只有可以反击的情况才可以 获取反击结果
        if (counterattack) {
            // 4.1 获取反击者的攻击力
            AttachResult counterattackResult = getOnceAttachResult(false, record, beAttachUnit, attachUnit,
                    beAttachUnitMes, beAttachLevelUnitMes, attachUnitLevelMes, beAttachAbility, abilityList);
            resultDto.setCounterattackResult(counterattackResult);
        }

        return resultDto;
    }

    /**
     * 获取一个攻击结果
     *
     * @param isInitiative         是否是主动攻击的
     * @param record               地图
     * @param attachUnit           发起攻击单位
     * @param beAttachUnit         被攻击单位
     * @param attachUnitMes        发起攻击单位信息
     * @param attachUnitLevelMes   发起攻击单位等级信息
     * @param beAttachLevelUnitMes 被攻击单位等级信息
     * @param abilityList          发起攻击单位能力列表
     * @param beAttachAbility      被攻击单位能力列表
     * @return
     */
    private AttachResult getOnceAttachResult(boolean isInitiative, UserRecord record, Unit attachUnit, Unit beAttachUnit, UnitMes attachUnitMes, UnitLevelMes attachUnitLevelMes,
                                             UnitLevelMes beAttachLevelUnitMes, List<Ability> abilityList, List<Ability> beAttachAbility) {
        // 1.1 获取到攻击者的攻击力
        AttachHandle attachHandle = AttachHandle.getDefaultHandle();
        AttributesPower attachPower = new AttributesPower(); // 保存攻击能力信息
        attachHandle.getAttachPower(record, attachUnit, attachUnitLevelMes, beAttachUnit, attachPower);
        // 1.2 根据能力判断攻击力
        for (Ability ability : abilityList) {
            AttachHandle abilityAttachHandle = AttachHandle.initAttachHandle(ability.getType());
            abilityAttachHandle.getAttachPower(record, attachUnit, attachUnitLevelMes, beAttachUnit, attachPower);
        }

        // 1.3 获取 被攻击者的对应的防御力
        String type = attachUnitMes.getAttackType();
        // 获取被攻击者的地形信息
        RegionMes regionMes = regionMesService.getRegionByType(AppUtil.getRegionByPosition(record, beAttachUnit).getType());
        AttributesPower defensePower = new AttributesPower();
        // 1.4 根据能力判断防御力 顺便判断是否包含
        DefenseHandle defenseHandle = DefenseHandle.getDefaultHandle();
        defenseHandle.getDefensePower(type, record, attachUnit, beAttachLevelUnitMes, regionMes, beAttachUnit, defensePower, beAttachAbility);
        for (Ability ability : beAttachAbility) {
            DefenseHandle abilityDefenseHandle = DefenseHandle.initAttachHandle(ability.getType());
            abilityDefenseHandle.getDefensePower(type, record, attachUnit, attachUnitLevelMes, regionMes, beAttachUnit, defensePower, beAttachAbility);

        }

        // 1.5. 根据攻防完善出一个主动攻击的伤害结果
        AttachResult attachResult = stuffAttachResult(isInitiative, attachPower, defensePower,attachUnit, beAttachUnit, abilityList, beAttachAbility);
        return attachResult;
    }
    /**
     * 获取
     * stuff -> 填充
     *
     * @return
     */
    private AttachResult stuffAttachResult(boolean isInitiative, AttributesPower attachPower, AttributesPower defensePower,Unit attachUnit, Unit beAttachUnit, List<Ability> abilityList, List<Ability> beAttachAbility) {

        int attachNum = attachPower.getNum();
        int defenseNum = defensePower.getNum();

        if (attachPower.getAddition() != null) {
            attachNum = (int) (attachNum * attachPower.getAddition());
        }

        if (defensePower.getAddition() != null) {
            defenseNum = (int) (defenseNum * defensePower.getAddition());
        }
        // 设置攻击情况
        AttachResult attachResult = new AttachResult();
        int harm = (attachNum - defenseNum) * AppUtil.getUnitLeft(attachUnit)/100;
        Integer[] attach;
        if (attachNum < defenseNum) {
            attach = new Integer[]{0};
        } else {
            if (harm > 100) {
                attach = new Integer[]{-1, 1, 0, 0};
            }else {
                attach = AppUtil.getArrayByInt(-1, harm);
            }
        }
        // 判断被攻击者是否死亡
        int left = AppUtil.getUnitLeft(beAttachUnit);
        attachResult.setAttach(attach);
        if (left < harm) {
            // 被攻击者已死
            attachResult.setDead(true);
            // 设置经验
            int ke;
            if (isInitiative) {
                ke = killExperience;
            }else {
                ke = antiKillExperience;
            }
            if (attachUnit.getExperience() != null) {
                attachResult.setEndExperience(attachUnit.getExperience() + ke);
            }else {
                attachResult.setEndExperience(ke);
            }
            // 判断是否有坟墓
            attachResult.setHaveTomb(true);
            for (Ability ability : beAttachAbility) {
                if (ability.getType().equals(AbilityEnum.UNDEAD.getType()) || ability.getType().equals(AbilityEnum.UNDEAD.getType())) {
                    attachResult.setHaveTomb(false);
                    break;
                }
            }
        } else {
            // 被攻击者未死
            attachResult.setEndExperience(attachUnit.getExperience() + attachExperience);
            attachResult.setDead(false);
            // 设置经验
            int ae;
            if (isInitiative) {
                ae = attachExperience;
            }else {
                ae = counterattackExperience;
            }
            if (attachUnit.getExperience() != null) {
                attachResult.setEndExperience(attachUnit.getExperience() + ae);
            }else {
                attachResult.setEndExperience(ae);
            }
            // 设置剩余生命
            int lastLeft = left - harm;
            attachResult.setLastLife(AppUtil.getArrayByInt(lastLeft));
            // 设置结束状态
            for (Ability ability : abilityList) {
                if (ability.getType().equals(AbilityEnum.POISONING.getType())) {
                    attachResult.setEndStatus(StateEnum.POISON.getType());
                    break;
                }
            }
        }

        // 判断是否升级
        switch (attachUnit.getLevel()) {
            case 0 :
                handleLevel(attachResult, level0);
                break;
            case 1 :
                handleLevel(attachResult, level1);
                break;
            case 2 :
                handleLevel(attachResult, level2);
                break;
            case 3 :
                handleLevel(attachResult, level3);
                break;
            case 4 :
                attachResult.setEndExperience(0);
                break;
        }

        return attachResult;
    }

    private void handleLevel(AttachResult attachResult, Integer level) {
        if (attachResult.getEndExperience() >= level) {
            attachResult.setLeaveUp(true);
            attachResult.setEndExperience(attachResult.getEndExperience() - level);
        }
    }
}
