package com.mihao.ancient_empire.handle.action;

import com.mihao.ancient_empire.common.util.EnumUtil;
import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.ActionEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.entity.mongo.UserRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取单位到达 目标点可以进行的action {@link ActionEnum}
 * 基础的行动处理类
 * 除了 攻击 还有 召唤 占领 破坏 加血 dly
 */
public class ActionHandle {

    protected ActionHandle() {
    }

    /**
     * 根据单位的 能力选择相应的能力处理器
     * @param abilityType
     * @return
     */
    public static ActionHandle initActionHandle(String abilityType) {
        AbilityEnum type = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (type) {
            case VILLAGE_GET:
                return new villageGetActionHandle();
            case SUMMONER:
                return new summonerActionHandle();
            case CASTLE_GET:
                return new castleGetActionHandle();
            case DESTROYER:
                return new destroyerActionHandle();
            case REPAIR:
                return new repairActionHandle();
            default:
                return null;
        }
    }

    public static ActionHandle getDefaultHandle() {
        return new ActionHandle();
    }

    /**
     * 获取单位的基础行为
     * @param positions 攻击范围
     * @param record
     * @param color
     * @param unitIndex
     * @param aimPoint
     * @return
     */
    public List<String> getAction(List<Position> positions, UserRecord record, String color, Integer unitIndex, Position aimPoint) {

        List<String> actions = new ArrayList<>();
        List<Army> ArmyList = record.getArmyList();
        for (Position p : positions) {
            for (Army army : ArmyList) {
                if (!army.getColor().equals(color)) {
                    List<Unit> units = army.getUnits();
                    for (Unit unit : units) {
                        if (!unit.isDead() && unit.getRow() == p.getRow() && unit.getColumn() == p.getColumn()) {
                            actions.add(ActionEnum.ATTACK.getType());
                            actions.add(ActionEnum.END.getType());
                            return actions;
                        }
                    }
                }
            }
        }
        actions.add(ActionEnum.END.getType());
        return actions;
    }
}
