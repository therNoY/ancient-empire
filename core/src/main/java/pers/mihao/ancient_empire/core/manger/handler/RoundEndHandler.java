package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyStatusInfoDTO;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.GameInfoDTO;
import pers.mihao.ancient_empire.core.dto.LifeChangeDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.start.StartStrategy;
import pers.mihao.ancient_empire.core.util.GameCoreUtil;

/**
 * 回合结束事件处理器 也是回合开始处理器  当一个回合结束时处理
 *
 * @Author mihao
 * @Date 2020/9/17 9:51
 */
public class RoundEndHandler extends CommonHandler {

    private static String POISON = "state.poison.decrease";

    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {

        // 结束回合先获取锁
        gameContext.getRecordLock().lock();

        gameContext.onRoundEnd(currArmy(), this);

        // 1.开始新的回合
        startNewRound(record());

        // 2.保存回合信息
        userRecordService.saveRecord(record());

        // 调用开始回合的钩子
        gameContext.beforeRoundStart(currArmy(), this);

        // 3.判断下局游戏是否是机器人
        if (currArmy().getPlayer() == null || currArmy().getPlayer().startsWith(CommonConstant.JOINER)) {
            robotManger.startRobot(gameContext);
        }

        gameContext.getRecordLock().unlock();
    }

    /**
     * 开启新的回合
     *
     * @param record
     */
    private void startNewRound(UserRecord record) {
        // 1. 上个回合 恢复单位的done状态
        List<UnitStatusInfoDTO> unitStatusInfoDTOS = new ArrayList<>();
        UnitStatusInfoDTO unitStatusInfoDTO;
        Unit lastRoundUnit;
        for (int i = 0; i < currArmy().getUnits().size(); i++) {
            lastRoundUnit = currArmy().getUnits().get(i);
            unitStatusInfoDTO = new UnitStatusInfoDTO(record().getCurrArmyIndex(), i);
            if (Boolean.TRUE.equals(lastRoundUnit.getDone())) {
                unitStatusInfoDTO.setDone(false);
                unitStatusInfoDTOS.add(unitStatusInfoDTO);
            }
        }
        if (unitStatusInfoDTOS.size() > 0) {
            changeUnitStatus(unitStatusInfoDTOS);
        }

        // 2. 开启新的回合
        int newArmyIndex = setNewArmy(record);
        ArmyStatusInfoDTO armyStatusInfoDTO = new ArmyStatusInfoDTO();
        record.setCurrArmyIndex(newArmyIndex);
        record.setCurrentRound(record.getCurrentRound() + 1);
        // 3.改变当前军队的资金
        List<Region> regions = record.getGameMap().getRegions();
        int addMoney = 0;
        for (BaseSquare square : regions) {
            if (currArmy().getColor().equals(square.getColor())) {
                addMoney += regionMesService.getRegionByTypeFromLocalCatch(square.getType()).getTax();
            }
        }
        armyStatusInfoDTO.setMoney(currArmy().getMoney() + addMoney);

        // 4.当前新的军队的生命变化以及状态变化
        changeUnitStatus();

        // 5.修改坟墓的状态 会消失
        changeTombStatus();

        // 6. 改变记录信息
        GameInfoDTO gameInfoDTO = new GameInfoDTO();
        gameInfoDTO.setCurrCamp(currArmy().getCamp());
        gameInfoDTO.setCurrColor(currArmy().getColor());
        gameInfoDTO.setCurrPlayer(currArmy().getPlayer());
        gameInfoDTO.setCurrArmyIndex(record().getCurrArmyIndex());

        // 新回合的提示
        JSONObject newRoundTip = new JSONObject();
        newRoundTip.put(ExtMes.MESSAGE, GameCoreUtil.getMessage("tip.newRecord", record().getCurrentRound(), addMoney));
        newRoundTip.put(ExtMes.COLOR, currArmy().getColor());

        commandStream()
            .toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_RECORD_INFO, ExtMes.RECORD_INFO, gameInfoDTO)
            .toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_ARMY_INFO, ExtMes.ARMY_INFO, armyStatusInfoDTO)
            .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_SYSTEM_NEWS, newRoundTip)
            .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_GAME_NEWS, ExtMes.MESSAGE,
            EnumUtil.valueOf(ColorEnum.class, currArmy().getColor()).getZhString() + "色方回合收入" + addMoney);

    }


    /**
     * 回合开始改变单位信息
     */
    private void changeUnitStatus() {
        List<LifeChangeDTO> lifeChanges = new ArrayList<>();
        List<UnitStatusInfoDTO> unitStatusChanges = new ArrayList<>();
        // 单位地形
        RegionInfo regionInfo;
        // 状态
        String status;
        // 生命变化
        LifeChangeDTO lifeChangeDTO;
        // 单位状态变化
        UnitStatusInfoDTO unitStatusInfoDTO;
        int descLife, lastLife;
        // 保证动画顺序
        boolean isDead = false;

        for (int i = 0; i < currArmy().getUnits().size(); i++) {
            Unit unit = currArmy().getUnits().get(i);
            status = unit.getStatus();
            unitStatusInfoDTO = new UnitStatusInfoDTO();
            lastLife = unit.getLife();

            // 如果状态数 > 0 就减1
            if (unit.getStatusPresenceNum() != null && unit.getStatusPresenceNum() > 0) {
                unit.setStatusPresenceNum(unit.getStatusPresenceNum() - 1);
                if (unit.getStatusPresenceNum() == 0) {
                    if (unitStatusInfoDTO.getArmyIndex() == null) {
                        unitStatusInfoDTO.setArmyIndex(record().getCurrArmyIndex());
                        unitStatusInfoDTO.setUnitIndex(i);
                    }
                    unitStatusInfoDTO.setStatus(StateEnum.NORMAL.type());
                    status = StateEnum.NORMAL.type();
                }
            }
            if (!StateEnum.POISON.type().equals(status)) {
                // 没有中毒根据地形回血
                regionInfo = getRegionInfoBySite(unit);
                lifeChangeDTO = StartStrategy.getInstance().getStartNewRoundLifeChange(regionInfo, unit, record());
                if (lifeChangeDTO != null) {
                    unitStatusInfoDTO.setArmyIndex(record().getCurrArmyIndex());
                    unitStatusInfoDTO.setUnitIndex(i);
                    unitStatusInfoDTO.setLife(unit.getLife() + AppUtil.getIntByArray(lifeChangeDTO.getAttach()));
                }
            } else {
                // 中毒不能回血 只能扣血
                descLife = AppConfig.getInt(POISON);
                lifeChangeDTO = new LifeChangeDTO();
                lifeChangeDTO.setRow(unit.getRow());
                lifeChangeDTO.setColumn(unit.getColumn());
                unitStatusInfoDTO.setArmyIndex(record().getCurrArmyIndex());
                unitStatusInfoDTO.setUnitIndex(i);
                if (descLife >= lastLife) {
                    lifeChangeDTO.setAttach(AppUtil.getArrayByInt(-1, lastLife));
                    isDead = true;
                } else {
                    unitStatusInfoDTO.setLife(lastLife - descLife);
                    lifeChangeDTO.setAttach(AppUtil.getArrayByInt(-1, descLife));
                }
            }

            // 如果生命有变化
            if (lifeChangeDTO != null && lifeChangeDTO.getRow() != null) {
                commandStream().toGameCommand()
                        .addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChangeDTO);
            }

            if (isDead) {
                // 单位死亡
                UnitInfo deadUnit = getUnitInfoByUnit(unit);
                sendUnitDeadCommend(deadUnit, new ArmyUnitIndexDTO(record().getCurrArmyIndex(), i));
                gameContext.onUnitDead(record().getCurrArmyIndex(), deadUnit, this);
            }

            // 根据是否设置index 判断单位状态是否有变化
            if (unitStatusInfoDTO.getArmyIndex() != null) {
                unitStatusChanges.add(unitStatusInfoDTO);
            }
        }

        if (unitStatusChanges.size() > 0) {
            changeUnitStatus(unitStatusChanges);
        }
    }

    private void changeTombStatus() {
        List<Tomb> tombs = record().getTombList();
        int maxPresenceNum = gameContext.getTombPresenceNum();
        List<Tomb> removeTomb = new ArrayList<>();
        for (Tomb tomb : tombs) {
            if (tomb.getPresenceNum() >= maxPresenceNum) {
                removeTomb.add(tomb);
            } else {
                tomb.setPresenceNum(tomb.getPresenceNum() + 1);
            }
        }
        for (Tomb tomb : removeTomb) {
            commandStream().toGameCommand().addCommand(GameCommendEnum.REMOVE_TOMB, tomb);
        }
    }

    /**
     * 改变当前回合 修改当前单位
     *
     * @param record
     * @return
     */
    public int setNewArmy(UserRecord record) {
        // 根据下一个顺序找单位 找不到 就轮到第一个
        int nextOrder = currArmy().getOrder() + 1;
        for (int i = 0; i < record.getArmyList().size(); i++) {
            Army army = record.getArmyList().get(i);
            if (army.getOrder() == nextOrder) {
                record.setCurrArmyIndex(i);
                return i;
            }
        }

        // 没有找到要开始新的循环 让order = 1的开始
        for (int i = 0; i < record.getArmyList().size(); i++) {
            Army army = record.getArmyList().get(i);
            if (army.getOrder() == 1) {
                record.setCurrArmyIndex(i);
                return i;
            }
        }
        throw new AeException();
    }
}
