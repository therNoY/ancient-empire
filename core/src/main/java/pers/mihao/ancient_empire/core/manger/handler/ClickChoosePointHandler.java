package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.defense.DefenseStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 点击攻击/召唤/治疗的选择指针框 此时确定单位的行动
 *
 * @version 1.0
 * @author mihao
 * @date 2020\10\7 0007 14:52
 */
public class ClickChoosePointHandler extends CommonHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 鼓舞状态伤害增加
     */
    private static final Integer EXCITED_HARM =  AppConfig.getInt("unitMes.excited.buff.harm");

    /**
     * @param gameEvent
     */
    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        // 此时相当于已经确定了行动 先更新单位的位置
        currUnit().setRow(currSite().getRow());
        currUnit().setColumn(currSite().getColumn());
        Unit unit = getCurrUnitFromArmy();
        unit.setRow(currSite().getRow());
        unit.setColumn(currSite().getColumn());

        // 更新新的单位信息
        currUnit().setRegionInfo(getRegionInfoBySite(gameContext.getReadyMoveSite()));

        // 根据地图状态机 判断处理方式
        switch (gameContext.getStatusMachine()) {
            case WILL_ATTACH:
                // 处理单位
                handlerAttachUnit(gameEvent);
                break;
            case WILL_ATTACH_REGION:
                handlerAttachRegion(gameEvent);
                break;
            case WILL_SUMMON:
                handlerSummon(gameEvent);
                break;
        }

    }

    /**
     * 处理单位攻击地形
     * 1. 改变单位经验
     * 2. 改变地形
     * 3. 结束移动
     * @param gameEvent
     */
    private void handlerAttachRegion(GameEvent gameEvent) {
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(currUnitArmyIndex());
        unitStatusInfoDTO.setExperience(currUnit().getExperience() + gameContext.getDestroyerExp());
        unitStatusInfoDTO.setUpdateCurr(true);
        changeUnitStatus(unitStatusInfoDTO);
        Region region = new Region();
        region.setType(RegionEnum.RUINS.type());
        int regionIndex = getRegionIndexBySite(gameEvent.getAimSite());
        changeCurrRegion(regionIndex);

        ShowAnimDTO showDeadAnimDTO = getShowAnim(gameEvent.getAimSite(), gameContext.getUserTemplate().getDeadAnimation());
        JSONObject showDeadAnim = new JSONObject();
        showDeadAnim.put(ExtMes.ANIM, showDeadAnimDTO);

        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.DIS_SHOW_ATTACH_AREA)
                .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_UNIT_DEAD, showDeadAnim);
        changeRegion(regionIndex, region);
        endCurrentUnit(currUnitArmyIndex());
    }

    /**
     * 处理单位攻击
     *
     * @param gameEvent
     */
    private void handlerAttachUnit(GameEvent gameEvent) {
        // 返回单位死亡的回调 不影响其他操作
        List<Pair<ArmyUnitIndexDTO, UnitInfo>> deadUnits = new ArrayList<>();

        // 获取攻击单位和被攻击单位的信息
        ArmyUnitIndexDTO attachArmyUnitIndexDTO = currUnitArmyIndex();

        Pair<Integer, UnitInfo> unitInfoPair = getUnitInfoFromMapBySite(gameEvent.getAimSite());
        UnitInfo beAttachUnit = unitInfoPair.getValue();
        Integer unitIndex = getUnitIndex(record().getArmyList().get(unitInfoPair.getKey()), beAttachUnit.getId());
        ArmyUnitIndexDTO beAttachArmyUnitIndexDTO = new ArmyUnitIndexDTO(unitInfoPair.getKey(), unitIndex);

        // 获取攻击结果
        AttachResultDTO attachResultDTO = getAttachResult(record().getCurrUnit(), beAttachUnit);

        // 不展示攻击区域
        commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ATTACH_AREA);

        // 展示攻击动画
        showAttachAnim(attachResultDTO.getAttachResult().getAttach(), attachArmyUnitIndexDTO, beAttachArmyUnitIndexDTO);

        // 判断攻击是否死亡
        if (attachResultDTO.getAttachResult().getDead()) {
            // 发送单位死亡命令
            sendUnitDeadCommend(beAttachUnit, beAttachArmyUnitIndexDTO);
            deadUnits.add(new Pair<>(beAttachArmyUnitIndexDTO, beAttachUnit));
        } else {
            // 没有死亡
            // 更新被攻击单位的状态 血量/经验/状态
            UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(beAttachArmyUnitIndexDTO);
            unitStatusInfoDTO.setLife(attachResultDTO.getAttachResult().getLastLife());
            unitStatusInfoDTO.setStatus(attachResultDTO.getAttachResult().getEndStatus());

            // 判断是否反击
            if (attachResultDTO.getAntiAttack()) {
                unitStatusInfoDTO.setExperience(attachResultDTO.getAntiAttackResult().getEndExperience());
                // 展示反击动画
                showAttachAnim(attachResultDTO.getAntiAttackResult().getAttach(), beAttachArmyUnitIndexDTO, attachArmyUnitIndexDTO);
            }

            changeUnitStatus(unitStatusInfoDTO);

        }
        if (attachResultDTO.getAntiAttack() && attachResultDTO.getAntiAttackResult().getDead()) {
            // 发送单位死亡命令
            sendUnitDeadCommend(currUnit(), currUnitArmyIndex());
            deadUnits.add(new Pair<>(currUnitArmyIndex(), currUnit()));
            gameContext.setSubStatusMachine(SubStatusMachineEnum.INIT);
            gameContext.setStatusMachine(StatusMachineEnum.INIT);
        }else {
            // 修改攻击者单位的状态
            UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(attachArmyUnitIndexDTO);
            if (attachResultDTO.getAntiAttack()) {
                unitStatusInfoDTO.setStatus(attachResultDTO.getAntiAttackResult().getEndStatus());
                unitStatusInfoDTO.setLife(attachResultDTO.getAntiAttackResult().getLastLife());
            }
            unitStatusInfoDTO.setExperience(attachResultDTO.getAttachResult().getEndExperience());
            unitStatusInfoDTO.setUpdateCurr(true);
            changeUnitStatus(unitStatusInfoDTO);
            // 结束
            endCurrentUnit(attachArmyUnitIndexDTO);
        }

        for (Pair<ArmyUnitIndexDTO, UnitInfo> pair : deadUnits) {
            gameContext.onUnitDead(pair.getKey().getArmyIndex(), pair.getValue(), this);
        }
    }


    /**
     * 处理召唤单位
     *
     * @param gameEvent
     */
    private void handlerSummon(GameEvent gameEvent) {

        Integer derivativeId = gameContext.getUserTemplate().getDerivativeId();
        if (derivativeId == null) {
            derivativeId = gameContext.getDefaultTemplate().getDerivativeId();
        }

        commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ATTACH_AREA);

        showSummonAnim(gameEvent.getAimSite());

        commandStream().toGameCommand().addOrderCommand(GameCommendEnum.REMOVE_TOMB, gameEvent.getAimSite());

        addNewUnitAtCurrArmy(derivativeId, gameEvent.getAimSite());

        // 处理状态
        UnitStatusInfoDTO unitStatusInfoDTO = new UnitStatusInfoDTO(currUnitArmyIndex());
        unitStatusInfoDTO.setExperience(currUnit().getExperience() + gameContext.getSummonExp());
        unitStatusInfoDTO.setUpdateCurr(true);
        changeUnitStatus(unitStatusInfoDTO);
        endCurrentUnit(currUnitArmyIndex());
    }


    /**
     * 处理获取单位攻击的结果
     *
     * @param attachUnit
     * @param beAttachUnit
     * @return
     */
    public AttachResultDTO getAttachResult(UnitInfo attachUnit, UnitInfo beAttachUnit) {
        // 最终返回的结果
        AttachResultDTO resultDto = new AttachResultDTO();

        // 1. 获取主动攻击结果
        AttachResult attachResult = getOnceAttachResult(attachUnit, beAttachUnit);

        // 2.处理攻击后的经验和升级
        handlerAttachExp(attachResult, gameContext.getAttachExp(), gameContext.getKillExp(), attachUnit, beAttachUnit);

        resultDto.setAttachResult(attachResult);

        // 3.判断是否反击
        if (!StateEnum.BLIND.type().equals(beAttachUnit.getStatus()) && !attachResult.getDead() &&
                AppUtil.isReach(attachUnit, beAttachUnit) && beAttachUnit.getUnitMes().getMinAttachRange() <= 1) {
            // 被致盲 无法反击  死了无法反击 挨着的攻击距离够
            // 3.1. 获取主动攻击结果
            AttachResult antiAttackResult = getOnceAttachResult(beAttachUnit, attachUnit);
            resultDto.setAntiAttack(true);
            // 3.2.处理攻击后的经验和升级
            handlerAttachExp(antiAttackResult, gameContext.getAntiAttackExp(), gameContext.getAntiKillExp(), attachUnit, beAttachUnit);
            resultDto.setAntiAttackResult(antiAttackResult);
        } else {
            resultDto.setAntiAttack(false);
        }
        return resultDto;
    }


    /**
     * 获取一个攻击结果
     *
     * @param attachUnit
     * @param beAttachUnit
     * @param isInitiative
     * @return
     */
    private AttachResult getOnceAttachResult(UnitInfo attachUnit, UnitInfo beAttachUnit) {

        log.info("============================获取一次攻击结果开始===========================================");

        // 1.1 获取到攻击者的攻击力
        AttributesPower attachPower = AttachStrategy.getInstance().getUnitAttachInfo(gameContext, attachUnit, beAttachUnit);

        // 1.2 获取 被攻击者的对应的防御力
        AttributesPower defensePower = DefenseStrategy.getInstance().getUnitDefenseInfo(gameContext, attachUnit, beAttachUnit);

        // 设置地形加成
        if (!beAttachUnit.getAbilities().contains(AbilityEnum.FLY.ability())) {
            // 飞行单位不享受地形加成
            defensePower.setNum(defensePower.getNum() + beAttachUnit.getRegionInfo().getBuff());
            log.info("{} 享受地形{} 防御增加加成{}", beAttachUnit.getType(), beAttachUnit.getRegionInfo().getType(), beAttachUnit.getRegionInfo().getBuff());
        }

        // 根据攻防生成一个主动攻击的伤害结果
        AttachResult attachResult = stuffAttachResult(attachPower, defensePower, attachUnit, beAttachUnit);

        // 判断修改被攻击者的状态
        if (attachUnit.getAbilities().contains(AbilityEnum.POISONING.ability()) &&
                !beAttachUnit.getAbilities().contains(AbilityEnum.POISONING.ability())) {
            attachResult.setEndStatus(StateEnum.POISON.type());
            beAttachUnit.setStatus(StateEnum.POISON.type());
        }

        return attachResult;
    }

    /**
     * 处理攻击后的单位的经验 和 升级问题
     *
     * @param attachResult
     * @param killExp
     * @param attachExp
     * @param attachUnit
     * @param beAttachUnit
     */
    private void handlerAttachExp(AttachResult attachResult, int attachExp, int killExp, UnitInfo attachUnit, UnitInfo beAttachUnit) {
        if (attachResult.getLastLife() <= 0) {
            // 被攻击者已死
            attachResult.setDead(true);
            // 设置经验
            attachResult.setEndExperience(attachUnit.getExperience() + killExp);
        } else {
            // 被攻击者未死
            attachResult.setEndExperience(attachUnit.getExperience() + attachExp);
            attachResult.setDead(false);
        }
    }


    /**
     * 根据攻击和防御的数据情况 生成攻击结果
     *
     * @return
     */
    private AttachResult stuffAttachResult(AttributesPower attachPower, AttributesPower defensePower, UnitInfo attachUnit, UnitInfo beAttachUnit) {
        // 设置攻击情况
        AttachResult attachResult = new AttachResult();

        int attachNum = attachPower.getNum();
        int defenseNum = defensePower.getNum();
        int attachUnitLeft = attachUnit.getLife();
        int beAttachUnitLeft = beAttachUnit.getLife();

        log.info("{} 经过加成后的攻击力{}， {} 最终防御力 {}", attachUnit.getType(), attachNum, beAttachUnit.getType(), defenseNum);

        // 伤害
        int harm = (attachNum - defenseNum) * attachUnitLeft / 100;
        // 根据攻击加成和防御加成重新设计 伤害值
        if (attachPower.getAddition() != null) {
            log.info("伤害加成 {} 原始伤害 ：{} 加成结果：{}", attachPower.getAddition(), harm, (int) (harm * attachPower.getAddition()));
            harm = (int) (harm * attachPower.getAddition());
        }
        // 鼓舞状态 加10攻击力
        if (StateEnum.EXCITED.type().equals(attachUnit.getStatus())){
            harm += EXCITED_HARM;
            log.info("攻击单位鼓舞状态 + {}攻击", EXCITED_HARM);
        }

        if (defensePower.getAddition() != null) {
            log.info("减伤加成 {} 原始伤害：{}, 减伤结果：{}", defensePower.getAddition(), harm, (int) (harm / defensePower.getAddition()));
            harm = (int) (harm / defensePower.getAddition());
        }
        // 判断如果无法破防的结果
        harm = harm < 0 ? 0 : harm;
        log.info("{}的血量:{}， 最终伤害{}", attachUnit.getType(), attachUnitLeft, harm);
        Integer[] attach;
        if (attachNum < defenseNum) {
            attach = new Integer[]{0};
        } else {
            if (harm >= beAttachUnitLeft) {
                attach = AppUtil.getArrayByInt(-1, beAttachUnitLeft);
            } else {
                attach = AppUtil.getArrayByInt(-1, harm);
            }
        }
        attachResult.setAttach(attach);
        // 设置剩余生命
        int lastLeft = beAttachUnitLeft - harm;
        attachResult.setLastLife(lastLeft);
        beAttachUnit.setLife(lastLeft);
        return attachResult;
    }



}
