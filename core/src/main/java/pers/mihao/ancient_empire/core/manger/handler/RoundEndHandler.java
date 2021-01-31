package pers.mihao.ancient_empire.core.manger.handler;

import java.util.ArrayList;
import java.util.List;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.config.AppConfig;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyStatusInfoDTO;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.GameInfoDTO;
import pers.mihao.ancient_empire.core.dto.LifeChangeDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 回合结束事件处理器  当一个回合结束时处理
 *
 * @Author mh32736
 * @Date 2020/9/17 9:51
 */
public class RoundEndHandler extends CommonHandler {

    private static String POISON = "state.poison.decrease";

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        // 1.保存回合信息
        userRecordService.saveRecord(record());

        // 2.开始新的回合
        startNewRound(record());

        // 3.判断下局游戏是否是机器人
        if (currArmy().getPlayer() == null) {
            robotManger.startRobot(gameContext);
        }
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
            commandAsyncStream().toGameCommand().changeUnitStatus(unitStatusInfoDTOS);
        }

        // 2. 开启新的回合
        int newArmyIndex = setNewArmy(record);
        ArmyStatusInfoDTO armyStatusInfoDTO = new ArmyStatusInfoDTO();
        record.setCurrArmyIndex(newArmyIndex);
        record.setCurrColor(currArmy().getColor());
        record.setCurrentRound(record.getCurrentRound() + 1);
        record.setCurrCamp(currArmy().getCamp());
        // 3.改变当前军队的资金
        List<Region> regions = record.getGameMap().getRegions();
        int addMoney = 0;
        for (BaseSquare square : regions) {
            if (currArmy().getColor().equals(square.getColor())) {
                addMoney += regionMesService.getRegionByType(square.getType()).getTax();
            }
        }
        armyStatusInfoDTO.setMoney(currArmy().getMoney() + addMoney);

        // 4.当前新的军队的生命变化以及状态变化
        changeUnitStatus();

        // 5. 改变记录信息
        GameInfoDTO gameInfoDTO = new GameInfoDTO();
        gameInfoDTO.setCurrCamp(currArmy().getCamp());
        gameInfoDTO.setCurrColor(currArmy().getColor());
        gameInfoDTO.setCurrPlayer(currArmy().getPlayer());
        gameInfoDTO.setCurrArmyIndex(record().getCurrArmyIndex());
        commandStream()
                .toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_RECORD_INFO, ExtMes.RECORD_INFO, gameInfoDTO)
                .toGameCommand().addOrderCommand(GameCommendEnum.CHANGE_ARMY_INFO, ExtMes.ARMY_INFO, armyStatusInfoDTO)
                .toGameCommand().addOrderCommand(GameCommendEnum.SHOW_GAME_NEWS, ExtMes.MESSAGE, currArmy().getColor() + "色方回合收入" + addMoney);

    }


    private void changeUnitStatus() {
        List<LifeChangeDTO> lifeChanges = new ArrayList<>();
        List<UnitStatusInfoDTO> unitStatusChanges = new ArrayList<>();
        // 单位地形
        Region square;
        // 状态
        String status;
        // 单位等级信息
        UnitLevelMes levelMes;
        // 生命变化
        LifeChangeDTO lifeChangeDTO;
        // 单位状态变化
        UnitStatusInfoDTO unitStatusInfoDTO;
        int restoreLife, regionRestore, descLife, lastLife;

        for (int i = 0; i < currArmy().getUnits().size(); i++) {
            Unit unit = currArmy().getUnits().get(i);
            square = getRegionBySite(unit);
            status = unit.getStatus();
            lifeChangeDTO = new LifeChangeDTO();
            unitStatusInfoDTO = new UnitStatusInfoDTO();
            levelMes = unitLevelMesService.getUnitLevelMes(unit.getTypeId(), unit.getLevel());
            lastLife = AppUtil.getUnitLife(unit);
            restoreLife = levelMes.getMaxLife() - lastLife;

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
                RegionMes regionMes = regionMesService.getRegionByType(square.getType());
                // 没有中毒根据地形回血
                if (restoreLife > 0
                        && (regionRestore = regionMes.getRestore()) > 0
                        && colorIsCamp(getRegionInfoBySite(unit).getColor())) {
                    lifeChangeDTO.setRow(unit.getRow());
                    lifeChangeDTO.setColumn(unit.getColumn());
                    unitStatusInfoDTO.setArmyIndex(record().getCurrArmyIndex());
                    unitStatusInfoDTO.setUnitIndex(i);
                    if (restoreLife < regionRestore) {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, restoreLife));
                    } else {
                        lifeChangeDTO.setAttach(AppUtil.getArrayByInt(10, regionRestore));
                    }
                }
            } else {
                // 中毒不能回血 只能扣血
                descLife = AppConfig.getInt(POISON);
                lifeChangeDTO.setRow(unit.getRow());
                lifeChangeDTO.setColumn(unit.getColumn());
                unitStatusInfoDTO.setArmyIndex(record().getCurrArmyIndex());
                unitStatusInfoDTO.setUnitIndex(i);
                if (descLife >= lastLife) {
                    lifeChangeDTO.setAttach(AppUtil.getArrayByInt(-1, lastLife));
                    unit.setDead(true);
                    // 单位死亡
                    sendUnitDeadCommend(getUnitInfoByUnit(unit), new ArmyUnitIndexDTO(record().getCurrArmyIndex(), i));

                } else {
                    lifeChangeDTO.setAttach(AppUtil.getArrayByInt(-1, descLife));
                }
            }

            // 如果生命有变化
            if (lifeChangeDTO.getRow() != null) {
                lifeChanges.add(lifeChangeDTO);
            }

            // 单位状态有变化
            if (unitStatusInfoDTO.getArmyIndex() != null) {
                unitStatusChanges.add(unitStatusInfoDTO);
            }
        }

        // 发送命令
        if (lifeChanges.size() > 0) {
            commandStream().toGameCommand().addOrderCommand(GameCommendEnum.LEFT_CHANGE, ExtMes.LIFE_CHANGE, lifeChanges);
        }
        if (unitStatusChanges.size() > 0) {
            commandStream().toGameCommand().changeUnitStatus(unitStatusChanges);
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
        throw new AncientEmpireException();
    }
}
