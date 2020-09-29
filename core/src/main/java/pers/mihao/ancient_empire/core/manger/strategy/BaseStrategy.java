package pers.mihao.ancient_empire.core.manger.strategy;

/**
 * 抽象策略
 */
public interface BaseStrategy {

    BaseStrategy initActionHandle(String abilityType);

    BaseStrategy getDefaultHandle();
}
