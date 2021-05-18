package pers.mihao.ancient_empire.core.manger.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pers.mihao.ancient_empire.common.util.Pair;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.dto.EndUnitDTO;
import pers.mihao.ancient_empire.core.manger.BaseHandler;

/**
 * 抽象能力处理类
 * @version 1.0
 * @author mihao
 * @date 2020\10\4 0004 8:46
 */
public abstract class AbstractStrategy<T> extends BaseHandler {

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
            classPathName = packName + CommonConstant.POINT + handlerName + className;
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


    /**
     * 填充处理结果
     * @param affectUnits
     * @param endUnitDTO
     * @param record
     */
    protected EndUnitDTO warpEndResult(List<Pair<Integer, Integer>> affectUnits, EndUnitDTO endUnitDTO, UserRecord record){
        return endUnitDTO;
    }
}
