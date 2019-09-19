package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.LifeChange;
import com.mihao.ancient_empire.dto.ws_dto.RespNewRoundDto;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.AbilityService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 回合结束Service
 */
@Service
public class WsNewRoundService {


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


    /**
     * 回合结束的Service
     *
     * @param uuid
     * @return
     */
    public RespNewRoundDto getNewRound(String uuid) {
        UserRecord record = recordService.getRecordById(uuid);
        List<String> campColors = AppUtil.getCampColors(record);
        // 1.改变当前回合 改变军队颜色
        int currentRound = record.getCurrentRound();
        int armSize = record.getArmyList().size();
        int armyIndex = currentRound % armSize;
        if (armyIndex == 0) {
            armyIndex = armSize;
        }
        Army currentArmy = record.getArmyList().get(armyIndex);
        record.setCurrColor(currentArmy.getColor());
        record.setCurrentRound(currentRound + 1);
        // 2.改变当前军队的资金
        List<BaseSquare> regions = record.getInitMap().getRegions();
        int addMoney = 0;
        for (BaseSquare square : regions) {
            if (square.getColor() != null && square.getColor().equals(currentArmy.getColor())) {
                if (square.getType().equals(RegionEnum.TOWN.getType())) {
                    addMoney = addMoney + townMoney;
                } else if (square.getType().equals(RegionEnum.CASTLE.getType())) {
                    addMoney = addMoney + castleMoney;
                }
            }
        }
        currentArmy.setMoney(currentArmy.getMoney() + addMoney);

        // 3.获取当前军队的生命变化以及状态变化
        List<LifeChange> lifeChanges = new ArrayList<>();
        for (Unit unit : currentArmy.getUnits()) {
            LifeChange lifeChange = new LifeChange();
            BaseSquare square = AppUtil.getRegionByPosition(record, unit);

            if (square.getType().equals(RegionEnum.TOWN)) {
                // 所处位置城镇
                if (square.getColor() != null && campColors.contains(square.getColor())) {
                    int lastLife = AppUtil.getUnitLeft(unit);
                    if (!unit.getStatus().equals(StateEnum.POISON.getType()) && lastLife < 100) {
                        restoreLife(lifeChange, townRestore, lastLife);
                    }
                }
            } else if (square.getType().equals(RegionEnum.CASTLE)) {
                // 所处位置城堡
                if (square.getColor() != null && campColors.contains(square.getColor())) {
                    int lastLife = AppUtil.getUnitLeft(unit);
                    if (!unit.getStatus().equals(StateEnum.POISON.getType()) && lastLife < 100) {
                        restoreLife(lifeChange, castleRestore, lastLife);
                    }
                }
            } else if (square.getType().equals(RegionEnum.TEMPLE)) {
                // 所处位置神殿
                int lastLife = AppUtil.getUnitLeft(unit);
                if (!unit.getStatus().equals(StateEnum.EXCITED.getType())) {
                    unit.setStatus(null);
                }
                if (lastLife < 100) {
                    restoreLife(lifeChange, templeRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.STOCK)) {
                // 所处位置寨子
                int lastLife = AppUtil.getUnitLeft(unit);
                if (!unit.getStatus().equals(StateEnum.POISON.getType()) && lastLife < 100) {
                    restoreLife(lifeChange, stockRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.SEA_HOUSE)) {
                // 所处位置是 海房
                int lastLife = AppUtil.getUnitLeft(unit);
                if (!unit.getStatus().equals(StateEnum.EXCITED.getType())) {
                    unit.setStatus(null);
                }
                if (lastLife < 100) {
                    restoreLife(lifeChange, seaHouseRestore, lastLife);
                }
            } else if (square.getType().equals(RegionEnum.REMAINS2)) {
                // 所处位置遗迹
                int lastLife = AppUtil.getUnitLeft(unit);
                if (!unit.getStatus().equals(StateEnum.POISON.getType()) && lastLife < 100) {
                    restoreLife(lifeChange, remainsRestore, lastLife);
                }
            }

            // 如果单位是中毒就改成掉血
            if (unit.getStatus().equals(StateEnum.POISON.getType())) {
                int lastLife = AppUtil.getUnitLeft(unit);
                decreaseLife(lifeChange, poisonDecrease, lastLife, unit, record);
            }

            // 如果状态数 > 0 就减1
            if (unit.getStatusPresenceNum() != null && unit.getStatusPresenceNum() > 0) {
                unit.setStatusPresenceNum(unit.getStatusPresenceNum() - 1);
            }

            lifeChanges.add(lifeChange);
        }

        RespNewRoundDto newRoundDto = new RespNewRoundDto(record, addMoney, lifeChanges);

        // 4.同步mongo
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
            lifeChange.setLastLife(AppUtil.getArrayByInt(lastLife - decreaseNum));
        }else {
            unit.setDead(true);
            // 死
            lifeChange.setChange(AppUtil.getArrayByInt(10, lastLife));
            // 判断是否有坟墓
            lifeChange.setHaveTomb(false);
            List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
            for (Ability ability : abilityList) {
                if (!ability.getType().equals(AbilityEnum.CASTLE_GET.getType()) && !ability.getType().equals(AbilityEnum.UNDEAD.getType())) {
                    record.getTomb().add(new Position(unit.getRow(), unit.getColumn()));
                    lifeChange.setHaveTomb(true);
                    break;
                }
            }
        }
    }
}