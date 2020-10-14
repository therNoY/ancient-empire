package pers.mihao.ancient_empire.core.manger.strategy;

/**
 * 抽象策略
 */
public interface Strategy {

    Strategy initActionHandle(String abilityType);

    Strategy getDefaultHandle();
}
