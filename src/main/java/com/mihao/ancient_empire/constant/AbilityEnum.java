package com.mihao.ancient_empire.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;
import com.mihao.ancient_empire.entity.Ability;

/**
 * 单位拥有的能力类型枚举
 */
public enum AbilityEnum implements BaseEnum {
    VILLAGE_GET,//  村庄捕获
    SHOOTER,//  神射手
    WATER_CLOSE,// 水之子
    SUMMONER, // 召唤师
    PURIFY, // 净化光环
    FLY, // 空军
    FOREST_CLOSE, // 森林之子
    POISONING, // 投毒者
    BLINDER, // 投毒者
    ASSAULT, // 突袭部队
    DESTROYER, // 破坏者
    REMOTE_DEFENSE, // 远程防御
    HILL_CLOSE, // 山之子
    WEAKER, // 虚弱光环
    MELEE_MASTER, // 近战大师
    CASTLE_GET, // 城堡捕获者
    REPAIR, // 维修者
    UNDEAD, // 亡灵
    HEALER, // 治疗者
    ATTACKER, // 攻击光环
    BLOOD_THIRSTY, // 血越少于强
    DEAD_KING; // 死亡领主


    /**
     * 用于比较
     * @return
     */
    public Ability ability() {
        return new Ability(this.type());
    }

}
