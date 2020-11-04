package pers.mihao.ancient_empire.core.handel.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;

/**
 * 获取单位到达 目标点可以进行的action {@link ActionEnum}
 * 基础的行动处理类
 * 除了 攻击 还有 召唤 占领 破坏 加血 dly
 */

public class ActionHandle {

    protected ActionHandle() {
    }

    public static ActionHandle actionHandle = null;

    /**
     * 根据单位的 能力选择相应的能力处理器
     * @param abilityType
     * @return
     */
    public static ActionHandle initActionHandle(String abilityType) {
        AbilityEnum type = EnumUtil.valueOf(AbilityEnum.class, abilityType);
        switch (type) {
            case VILLAGE_GET:
                return VillageGetActionHandle.instance();
            case SUMMONER:
                return SummonerActionHandle.instance();
            case CASTLE_GET:
                return CastleGetActionHandle.instance();
            case DESTROYER:
                return DestroyerActionHandle.instance();
            case REPAIR:
                return RepairActionHandle.instance();
            default:
                return null;
        }
    }

    public static ActionHandle getDefaultHandle() {
        if (actionHandle == null) {
            return new ActionHandle();
        }
        return actionHandle;
    }

    /**
     * 获取单位的基础行为
     * @param positions 攻击范围
     * @param record
     * @param camp
     * @param unitIndex
     * @param aimPoint
     * @return
     */
    public List<String> getAction(List<Position> positions, UserRecord record, Integer camp, Integer unitIndex, Position aimPoint) {
        List<String> actions = new ArrayList<>();
        List<Army> ArmyList = record.getArmyList();
        for (Position p : positions) {
            for (Army army : ArmyList) {
                if (!army.getCamp().equals(camp)) {
                    List<Unit> units = army.getUnits();
                    for (Unit unit : units) {
                        if (!unit.isDead() && unit.getRow().equals(p.getRow()) && unit.getColumn()
                            .equals(p.getColumn())) {
                            actions.add(ActionEnum.ATTACK.type());
                            actions.add(ActionEnum.END.type());
                            return actions;
                        }
                    }
                }
            }
        }
        actions.add(ActionEnum.END.type());
        return actions;
    }
}
