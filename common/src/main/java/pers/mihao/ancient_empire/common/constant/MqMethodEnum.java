package pers.mihao.ancient_empire.common.constant;

/*mq 方法枚举*/
public enum MqMethodEnum {
    ACTION_ATTACH, // 处理攻击的结果
    ACTION_REPAIR_OCP, // 处理占领/修复的结果
    ACTION_SUMMON, // 处理 召唤的结果
    ACTION_END, // 处理 结束回合的结果
    BUY_UNIT, // 处理购买单位的结果
    END_ROUND, // 处理结束回合
}
