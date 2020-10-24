package pers.mihao.ancient_empire.core.manger.strategy.action;

import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.manger.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取单位到达 目标点可以进行的action {@link ActionEnum}
 * 基础的行动处理类
 * 除了 攻击 还有 召唤 占领 破坏 加血 dly
 */

public class ActionStrategy extends AbstractStrategy<ActionStrategy> {

    public static ActionStrategy actionStrategy = null;


    public static ActionStrategy getInstance() {
        if (actionStrategy == null) {
            actionStrategy = new ActionStrategy();
        }
        return actionStrategy;
    }

    /**
     * 获取单位action
     * @param positions 攻击范围
     * @param record
     * @param aimPoint
     * @return
     */
    public Set<String> getActionList(List<Site> positions, UserRecord record, Site aimPoint){
        /**
         * 致盲只能移动结束
         */
        Set<String> actionSet = new HashSet<>();
        if (StateEnum.BLIND.type().equals(record.getCurrUnit().getStatus())){
            actionSet.add(ActionEnum.END.type());
            return actionSet;
        }
        // 获取默认处理
        List<String> defaultActions = getAction(positions, record, aimPoint);
        actionSet.addAll(defaultActions);
        // 获取特殊处理
        for (ActionStrategy actionStrategy : getAbilityStrategy(record.getCurrUnit().getAbilities())) {
            // 根据能力获取所有的行为
            if (actionStrategy != null) {
                List<String> actions = actionStrategy.getAction(positions, record, aimPoint);
                actionSet.addAll(actions);
            }
        }
        return actionSet;
    }


    /**
     * 获取单位的基础行为
     * @param unitIndex
     * @param sites 攻击范围
     * @param record
     * @param aimSite
     * @return
     */
    protected List<String> getAction(List<Site> sites, UserRecord record, Site aimSite) {
        List<String> actions = new ArrayList<>();
        List<Army> ArmyList = record.getArmyList();
        for (Army army : ArmyList) {
            if (!army.getCamp().equals(record.getCurrCamp())) {
                List<Unit> units = army.getUnits();
                for (Unit unit : units) {
                    if (!unit.isDead() && sites.contains(unit)) {
                        actions.add(ActionEnum.ATTACK.type());
                        break;
                    }
                }
            }
        }
        actions.add(ActionEnum.END.type());
        return actions;
    }
}
