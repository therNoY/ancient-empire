package pers.mihao.ancient_empire.core.websocket.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.core.dto.LifeChange;
import pers.mihao.ancient_empire.core.dto.RespNewRoundDto;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * 回合结束Service
 */
@Service
public class WsEndRoundService {


    @Autowired
    UserRecordService recordService;
    @Autowired
    AbilityService abilityService;
    @Autowired
    MqHelper mqHelper;

    @Value("${region.town.money}")
    Integer townMoney;
    @Value("${region.town.castle}")
    Integer castleMoney;
    @Value("${region.town.restore}")
    Integer townRestore;
    @Value("${region.castle.restore}")
    Integer castleRestore;
    @Value("${region.temple.restore}")
    Integer templeRestore;
    @Value("${region.stock.restore}")
    Integer stockRestore;
    @Value("${region.seaHouse.restore}")
    Integer seaHouseRestore;
    @Value("${region.remains.restore}")
    Integer remainsRestore;
    @Value("${state.poison.decrease}")
    Integer poisonDecrease;


    public RespNewRoundDto getNewRound(String uuid) {
        UserRecord record = recordService.getRecordById(uuid);
        return getNewRound(record);
    }

    /**
     * 回合结束的Service
     *
     * @param record
     * @return
     */
    public RespNewRoundDto getNewRound(UserRecord record) {
        List<String> campColors = AppUtil.getCampColors(record);

        // 1. 恢复当前单位的非石化状态
        Army cArmy = AppUtil.getCurrentArmy(record);
        for (Unit unit : cArmy.getUnits()) {
            unit.setDone(false);
        }

        // 2.改变当前回合 改变军队颜色
        Army currentArmy = null;
        int currentRound = record.getCurrentRound() + 1;
        int nextOrder = AppUtil.getCurrentArmy(record).getOrder() + 1;
        for (Army army : record.getArmyList()) {
            if (army.getOrder() == nextOrder) {
                currentArmy = army;
            }
        }

        // 2.1 没有找到要开始新的循环 让order = 1的开始
        if (currentArmy == null) {
            for (Army army : record.getArmyList()) {
                if (army.getOrder() == 1) {
                    currentArmy = army;
                }
            }
        }

        record.setCurrColor(currentArmy.getColor());
        record.setCurrentRound(currentRound);
        record.setCurrCamp(currentArmy.getCamp());
        // 3.改变当前军队的资金
        List<Region> regions = record.getGameMap().getRegions();
        int addMoney = 0;
        for (BaseSquare square : regions) {
            if (square.getColor() != null && square.getColor().equals(currentArmy.getColor())) {
                if (square.getType().equals(RegionEnum.TOWN.type())) {
                    addMoney = addMoney + townMoney;
                } else if (square.getType().equals(RegionEnum.CASTLE.type())) {
                    addMoney = addMoney + castleMoney;
                }
            }
        }
        currentArmy.setMoney(currentArmy.getMoney() + addMoney);

        // 4.获取当前军队的生命变化以及状态变化
        List<LifeChange> lifeChanges = new ArrayList<>();
        for (Unit unit : currentArmy.getUnits()) {
            LifeChange lifeChange = new LifeChange();
            BaseSquare square = GameCoreHelper.getRegionByPosition(record, unit);
            String status = unit.getStatus();
            if (square.getType().equals(RegionEnum.TOWN)) {
                // 所处位置城镇
                if (square.getColor() != null && campColors.contains(square.getColor())) {
                    int lastLife = AppUtil.getUnitLeft(unit);
                    if (status !=null && !status.equals(StateEnum.POISON.type()) && lastLife < 100) {
                        restoreLife(lifeChange, townRestore, lastLife);
                    }
                }
            } else if (square.getType().equals(RegionEnum.CASTLE)) {
                // 所处位置城堡
                if (square.getColor() != null && campColors.contains(square.getColor())) {
                    int lastLife = AppUtil.getUnitLeft(unit);
                    if (status !=null && !status.equals(StateEnum.POISON.type()) && lastLife < 100) {
                        restoreLife(lifeChange, castleRestore, lastLife);
                    }
                }
            } else if (square.getType().equals(RegionEnum.TEMPLE)) {
                // 所处位置神殿
                int lastLife = AppUtil.getUnitLeft(unit);
                if (status !=null && !status.equals(StateEnum.EXCITED.type())) {
                    unit.setStatus(null);
                }
                if (lastLife < 100) {
                    restoreLife(lifeChange, templeRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.STOCK)) {
                // 所处位置寨子
                int lastLife = AppUtil.getUnitLeft(unit);
                if (status !=null && !status.equals(StateEnum.POISON.type()) && lastLife < 100) {
                    restoreLife(lifeChange, stockRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.SEA_HOUSE)) {
                // 所处位置是 海房
                int lastLife = AppUtil.getUnitLeft(unit);
                if (status !=null && !status.equals(StateEnum.EXCITED.type())) {
                    unit.setStatus(null);
                }
                if (lastLife < 100) {
                    restoreLife(lifeChange, seaHouseRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.REMAINS2)) {
                // 所处位置遗迹
                int lastLife = AppUtil.getUnitLeft(unit);
                if (status !=null && !status.equals(StateEnum.POISON.type()) && lastLife < 100) {
                    restoreLife(lifeChange, remainsRestore, lastLife);
                }
            }

            // 如果单位是中毒就改成掉血
            if (status !=null && status.equals(StateEnum.POISON.type())) {
                int lastLife = AppUtil.getUnitLeft(unit);
                decreaseLife(lifeChange, poisonDecrease, lastLife, unit, record);
            }

            // 如果状态数 > 0 就减1
            if (unit.getStatusPresenceNum() != null && unit.getStatusPresenceNum() > 0) {
                unit.setStatusPresenceNum(unit.getStatusPresenceNum() - 1);
                if (unit.getStatusPresenceNum() == 0) {
                    lifeChange.setState(StateEnum.NORMAL.type());
                    unit.setStatus(null);
                }
            }

            lifeChanges.add(lifeChange);
        }

        RespNewRoundDto newRoundDto = new RespNewRoundDto(record, addMoney, lifeChanges);

        // 5.同步mongo
        mqHelper.sendMongoCdr(MqMethodEnum.END_ROUND, record);
        return newRoundDto;
    }

    void restoreLife(LifeChange lifeChange, int restoreNum, int lastLife) {
        if (restoreNum + lastLife <= 100) {
            // 恢复不满
            lifeChange.setChange(AppUtil.getArrayByInt(10, restoreNum));
            lifeChange.setLastLife(AppUtil.getArrayByInt(restoreNum + lastLife));
        }else {
            // 恢复不满
            lifeChange.setChange(AppUtil.getArrayByInt(10, 100 - lastLife));
            lifeChange.setLastLife(AppUtil.getArrayByInt(100));
        }
    }

    /**
     * 减少单位生命值 如果为0 就增阿坟墓
     */
    void decreaseLife(LifeChange lifeChange, Integer decreaseNum, int lastLife, Unit unit, UserRecord record) {
        if (lastLife > decreaseNum) {
            // 没死
            lifeChange.setChange(AppUtil.getArrayByInt(-1, decreaseNum));
            Integer[] lastLifes = AppUtil.getArrayByInt(lastLife - decreaseNum);
            unit.setLife(lastLifes);
            lifeChange.setLastLife(lastLifes);
        }else {
            unit.setDead(true);
            // 死
            lifeChange.setChange(AppUtil.getArrayByInt(10, lastLife));
            // 判断是否有坟墓
            lifeChange.setHaveTomb(false);
            List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
            for (Ability ability : abilityList) {
                if (!ability.getType().equals(AbilityEnum.CASTLE_GET.type()) && !ability.getType().equals(AbilityEnum.UNDEAD.type())) {
                    record.getTomb().add(new Position(unit.getRow(), unit.getColumn()));
                    lifeChange.setHaveTomb(true);
                    break;
                }
            }
        }
    }
}
