package pers.mihao.ancient_empire.core.eums;

/**
 * 游戏状态枚举
 * @Author mihao
 * @Date 2020/9/11 21:55
 */
public enum GameStatusEnum {

    /**
     * 没有行为
     */
    NO_ACTION,
    /**
     * 展示移动区域
     */
    SHOW_MOVE_AREA,
    /**
     * 移动
     */
    MOVE,
    /**
     * 展示行为
     */
    SHOW_ACTION,
    /**
     * 想要攻击
      */
    WILL_ATTACH,
    /**
     * 在攻击
     */
    ATTACHING,
    /**
     * 等级提升
     */
    LEVEL_UP,
    /**
     * 攻击结束
     */
    ATTACH_DONE,
    /**
     * 行动结束
     */
    ACTION_DONE,
    /**
     * 二次移动
     */
    SECOND_MOVE,
    /**
     * 生命改变
     */
    LIFE_CHANGE,

    /**
     * 将要召唤
     */
    WILL_SUMMON,
    /**
     * 正在召唤
     */
    SUMMONING,
    /**
     * 将要结束
     */
    WILL_END
    
}
