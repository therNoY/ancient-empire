package pers.mihao.ancient_empire.base.enums;

import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.common.enums.BaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 单位拥有的能力类型枚举
 */
public enum AbilityEnum implements BaseEnum {


    /**
     * 村庄捕获
     */
    VILLAGE_GET,
    /**
     * 神射手
     */
    SHOOTER,
    /**
     * 水之子
     */
    WATER_CLOSE,
    /**
     * 召唤师
     */
    SUMMONER,
    /**
     * 净化光环
     */
    PURIFY,
    /**
     * 空军
     */
    FLY,
    /**
     * 森林之子
     */
    FOREST_CLOSE,
    /**
     * 毒物
     */
    POISONING,
    /**
     * 致盲
     */
    BLINDER,
    /**
     * 机动部队
     */
    ASSAULT,
    /**
     * 破坏者
     */
    DESTROYER,
    /**
     * 远程防御
     */
    REMOTE_DEFENSE,
    /**
     * 山之子
     */
    HILL_CLOSE,
    /**
     * 虚弱光环
     */
    WEAKER,
    /***
     * 近战大师
     */
    MELEE_MASTER,
    /**
     * 城堡捕获者
     */
    CASTLE_GET,
    /**
     * 维修者
     */
    REPAIR,
    /**
     * 亡灵
     */
    UNDEAD,
    /**
     * 治疗者
     */
    HEALER,
    /**
     * 攻击光环
     */
    ATTACKER,
    /**
     * 血越少于强
     */
    BLOOD_THIRSTY,
    /**
     * 死亡领主
     */
    DEAD_KING;


    private final static Map<AbilityEnum, Ability> map = new HashMap(16);

    /**
     * 用于比较
     *
     * @return
     */
    public Ability ability() {
        Ability ability = map.get(this);
        if (ability == null) {
            ability = new Ability(this.type());
            map.put(this, ability);
        }
        return ability;
    }
}
