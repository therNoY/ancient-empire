package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.*;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.attach.AttachHandle;
import com.mihao.ancient_empire.handle.defense.DefenseHandle;
import com.mihao.ancient_empire.service.*;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import com.mihao.ancient_empire.util.UserRecordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 获取单位攻击结果
 */
@Service
public class WsAttachResultService {

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
    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    MqHelper mqHelper;

    @Value("${experience.attach}")
    Integer attachExperience;
    @Value("${experience.counterattack}")
    Integer counterattackExperience;
    @Value("${experience.kill}")
    Integer killExperience;
    @Value("${experience.antikill}")
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
     * 获取攻击结果
     * 不包括 攻击 房屋 攻击房屋之后才考虑
     *
     * @param uuid         record 的uuid
     * @param reqAttachDto
     * @return
     */
    public RespAttachResultDto getAttachResult(String uuid, ReqAttachDto reqAttachDto) {
        // 最终返回的结果
        RespAttachResultDto resultDto = new RespAttachResultDto();
        AttachSituation attachSituation = new AttachSituation();

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
        AttachResult attachResult = getOnceAttachResult(true, attachSituation, record, attachUnit, beAttachUnit,
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
            AttachResult counterattackResult = getOnceAttachResult(false, attachSituation, record, beAttachUnit, attachUnit,
                    beAttachUnitMes, beAttachLevelUnitMes, attachUnitLevelMes, beAttachAbility, abilityList);
            resultDto.setCounterattackResult(counterattackResult);
        }

        resultDto.setAttachSituation(attachSituation);

        // 判断是否可以二次移动
        SecondMoveDto secondMoveDto = moveAreaService.getSecondMove(attachUnit, record, reqAttachDto);
        if (secondMoveDto == null) {
            resultDto.setSecondMove(false);
        }else {
            resultDto.setSecondMove(secondMoveDto.getSecondMove());
            resultDto.setMoveArea(secondMoveDto.getMoveArea());
        }
        // 通知mq 处理攻击后的结果
        mqHelper.sendMongoCdr(MqMethodEnum.ACTION_ATTACH, record);
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
    private AttachResult getOnceAttachResult(boolean isInitiative, AttachSituation attachSituation, UserRecord record, Unit attachUnit, Unit beAttachUnit,
                                             UnitMes attachUnitMes, UnitLevelMes attachUnitLevelMes, UnitLevelMes beAttachLevelUnitMes, List<Ability> abilityList, List<Ability> beAttachAbility) {
        // 1.1 获取到攻击者的攻击力
        AttachHandle attachHandle = AttachHandle.getDefaultHandle();
        AttributesPower attachPower = new AttributesPower(); // 保存攻击能力信息
        attachHandle.getAttachPower(record, attachUnit, attachUnitLevelMes, beAttachUnit, attachPower);
        int baseAttach = attachPower.getNum();
        // 1.2 根据能力判断攻击力
        boolean isChangeStatus = false;
        for (Ability ability : abilityList) {
            if (!isChangeStatus && ability.getType().equals(AbilityEnum.POISONING.getType())) {
                isChangeStatus = true;
            }
            AttachHandle abilityAttachHandle = AttachHandle.initAttachHandle(ability.getType());
            abilityAttachHandle.getAttachPower(record, attachUnit, attachUnitLevelMes, beAttachUnit, attachPower);
        }
        // 设置攻击加成情况
        if (attachPower.getNum() > baseAttach || (attachPower.getAddition() != null && attachPower.getAddition() > 1)) {
            if (isInitiative) {
                attachSituation.setAttachUp(1);
            } else {
                attachSituation.setBeAttackUp(1);
            }
        } else if (attachPower.getNum() < baseAttach || (attachPower.getAddition() != null && attachPower.getAddition() < 1)) {
            if (isInitiative) {
                attachSituation.setAttachUp(-1);
            } else {
                attachSituation.setBeAttackUp(-1);
            }
        } else {
            if (isInitiative) {
                attachSituation.setAttachUp(0);
            } else {
                attachSituation.setBeAttackUp(0);
            }
        }

        // 1.3 获取 被攻击者的对应的防御力
        String type = attachUnitMes.getAttackType();
        // 获取被攻击者的地形信息
        RegionMes regionMes = regionMesService.getRegionByType(AppUtil.getRegionByPosition(record, beAttachUnit).getType());
        AttributesPower defensePower = new AttributesPower();
        // 1.4 根据能力判断防御力 顺便判断是否包含
        DefenseHandle defenseHandle = DefenseHandle.getDefaultHandle();
        defenseHandle.getDefensePower(type, record, attachUnit, beAttachLevelUnitMes, regionMes, beAttachUnit, defensePower, beAttachAbility);
        int baseDefense = defensePower.getNum();
        // 飞行单位不享受地形加成
        if (!beAttachAbility.contains(new Ability(AbilityEnum.FLY.getType()))) {
            defensePower.setNum(defensePower.getNum() + regionMes.getBuff());
            log.info("{} 享受地形{} 加成{}", beAttachUnit.getType(), regionMes.getType(), regionMes.getBuff());
        }
        for (Ability ability : beAttachAbility) {
            DefenseHandle abilityDefenseHandle = DefenseHandle.initAttachHandle(ability.getType());
            abilityDefenseHandle.getDefensePower(type, record, attachUnit, attachUnitLevelMes, regionMes, beAttachUnit, defensePower, beAttachAbility);

        }

        // 设置防御加成情况
        if (defensePower.getNum() > baseDefense || (defensePower.getAddition() != null && defensePower.getAddition() > 1)) {
            if (isInitiative) {
                attachSituation.setDefenseUp(1);
            } else {
                attachSituation.setBeDefenseUp(1);
            }
        } else if (defensePower.getNum() < baseDefense || (defensePower.getAddition() != null && defensePower.getAddition() < 1)) {
            if (isInitiative) {
                attachSituation.setDefenseUp(-1);
            } else {
                attachSituation.setBeDefenseUp(-1);
            }
        } else {
            if (isInitiative) {
                attachSituation.setDefenseUp(0);
            } else {
                attachSituation.setBeDefenseUp(0);
            }
        }

        // 1.5. 根据攻防完善出一个主动攻击的伤害结果
        AttachResult attachResult = stuffAttachResult(isInitiative, record, attachPower, defensePower, attachUnit, beAttachUnit, abilityList, beAttachAbility);

        if (isChangeStatus && !beAttachAbility.contains(new Ability(AbilityEnum.POISONING.getType()))) {
            attachResult.setEndStatus(StateEnum.POISON.getType());
            beAttachUnit.setStatus(StateEnum.POISON.getType());
            beAttachUnit.setStatusPresenceNum(3);
        }

        return attachResult;
    }

    /**
     * 获取
     * stuff -> 填充
     *
     * @return
     */
    private AttachResult stuffAttachResult(boolean isInitiative, UserRecord record, AttributesPower attachPower, AttributesPower defensePower,
                                           Unit attachUnit, Unit beAttachUnit, List<Ability> abilityList, List<Ability> beAttachAbility) {
        int attachNum = attachPower.getNum();
        int defenseNum = defensePower.getNum();
        int left = AppUtil.getUnitLeft(beAttachUnit);
        if (attachPower.getAddition() != null) {
            attachNum = (int) (attachNum * attachPower.getAddition());
        }
        if (defensePower.getAddition() != null) {
            defenseNum = (int) (defenseNum * defensePower.getAddition());
        }

        log.info("{} 经过加成后的攻击力{}， {}最终防御力{}", attachUnit.getType(), attachNum, beAttachUnit.getType(), defenseNum);
        // 设置攻击情况
        AttachResult attachResult = new AttachResult();
        int harm = (attachNum - defenseNum) * AppUtil.getUnitLeft(attachUnit) / 100;
        log.info("{} 血量 {}， 最终伤害{}", attachUnit.getType(), AppUtil.getUnitLeft(attachUnit), harm);
        Integer[] attach;
        if (attachNum < defenseNum) {
            attach = new Integer[]{0};
        } else {
            if (harm >= left) {
                attach = AppUtil.getArrayByInt(-1, left);
            } else {
                attach = AppUtil.getArrayByInt(-1, harm);
            }
        }
        // 判断被攻击者是否死亡
        attachResult.setAttach(attach);
        if (left < harm) {
            // 被攻击者已死
            attachResult.setDead(true);
            UserRecordUtil.updateUnit(record, beAttachUnit.getId(), unit -> {
                unit.setDead(true);
            });
            // 设置经验
            int ke;
            if (isInitiative) {
                ke = killExperience;
            } else {
                ke = antiKillExperience;
            }
            if (attachUnit.getExperience() != null) {
                attachResult.setEndExperience(attachUnit.getExperience() + ke);
            } else {
                attachResult.setEndExperience(ke);
            }
            // 判断是否有坟墓
            attachResult.setHaveTomb(false);
            for (Ability ability : beAttachAbility) {
                if (!ability.getType().equals(AbilityEnum.CASTLE_GET.getType()) && !ability.getType().equals(AbilityEnum.UNDEAD.getType())) {
                    record.getTomb().add(new Position(beAttachUnit.getRow(), beAttachUnit.getColumn()));
                    attachResult.setHaveTomb(true);
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
            } else {
                ae = counterattackExperience;
            }
            if (attachUnit.getExperience() != null) {
                attachResult.setEndExperience(attachUnit.getExperience() + ae);
            } else {
                attachResult.setEndExperience(ae);
            }
            // 设置剩余生命
            Integer[] lastLeft = AppUtil.getArrayByInt(left - harm);
            attachResult.setLastLife(lastLeft);

            UserRecordUtil.updateUnit(record, beAttachUnit.getId(), unit -> {
                unit.setLife(lastLeft);
                unit.setStatus(attachResult.getEndStatus());
            });
        }
        // 判断是否升级
        switch (attachUnit.getLevel()) {
            case 0:
                handleLevel(attachResult, level0);
                break;
            case 1:
                handleLevel(attachResult, level1);
                break;
            case 2:
                handleLevel(attachResult, level2);
                break;
            case 3:
                handleLevel(attachResult, level3);
                break;
            case 4:
                attachResult.setEndExperience(0);
                break;
        }

        if (attachResult.getLeaveUp() != null && attachResult.getLeaveUp()) {
            UserRecordUtil.updateUnit(record, attachUnit.getId(), unit -> {
                unit.setLevel(unit.getLevel() + 1);
                unit.setExperience(attachResult.getEndExperience());
            });
        } else {
            UserRecordUtil.updateUnit(record, attachUnit.getId(), unit -> {
                unit.setLevel(unit.getLevel());
                unit.setExperience(attachResult.getEndExperience());
            });
        }

        return attachResult;
    }

    /**
     * 处理单位升级
     *
     * @param attachResult
     * @param level
     */
    private void handleLevel(AttachResult attachResult, Integer level) {
        if (attachResult.getEndExperience() >= level) {
            attachResult.setLeaveUp(true);
            attachResult.setEndExperience(attachResult.getEndExperience() - level);
        }
    }

}
