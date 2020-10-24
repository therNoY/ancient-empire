package pers.mihao.ancient_empire.base.enums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 单位buff枚举
 * 失明, 兴奋, 中毒, 虚弱
 */
public enum StateEnum implements BaseEnum {

    /**
     * 致盲
     */
    BLIND(true),
    /**
     * 兴奋
     */
    EXCITED(false),
    /**
     * 中毒
     */
    POISON(true),
    /**
     * 虚弱
     */
    WEAK(true),
    /**
     * 正常
     */
    NORMAL(false);

    public final Boolean isDeBuff;

    StateEnum(Boolean isDeBuff) {
        this.isDeBuff = isDeBuff;
    }
}
