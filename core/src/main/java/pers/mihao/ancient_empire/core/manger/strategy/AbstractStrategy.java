package pers.mihao.ancient_empire.core.manger.strategy;

import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\4 0004 8:46
 */
public abstract class AbstractStrategy<T> {

    /**
     * 每个能力对应的处理类
     */
    protected Map<String, AbstractStrategy> abilityStrategy = new HashMap<>(16);

    public AbstractStrategy() {
        String packName = this.getClass().getPackage().getName();
        String className = this.getClass().getSimpleName();
        String handlerName, classPathName;
        for (AbilityEnum abilityEnum : AbilityEnum.values()) {
            handlerName = StringUtil.underscoreToHump(abilityEnum.toString(), true);
            classPathName = packName + BaseConstant.POINT + handlerName + className;
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(classPathName);
                abilityStrategy.put(abilityEnum.type(), (AbstractStrategy) clazz.newInstance());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 根据获取单位的策略
     * @param abilities
     * @return
     */
    protected List<T> getAbilityStrategy(List<Ability> abilities){
        List<T> strategies = new ArrayList<>();
        for (Ability ab : abilities) {
            T t = (T) abilityStrategy.get(ab.getType());
            if (t != null) {
                strategies.add(t);
            }
        }
        return strategies;
    }

    protected int getSiteLength(Site p1, Site p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

    protected int getSiteLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

    /**
     * 填充处理结果
     * @param affectUnits
     * @param endUnitDTO
     * @param endUnitDTO1
     */
    protected EndUnitDTO warpEndResult(List<Pair<Integer, Integer>> affectUnits, EndUnitDTO endUnitDTO, UserRecord record){
        return endUnitDTO;
    }
}
