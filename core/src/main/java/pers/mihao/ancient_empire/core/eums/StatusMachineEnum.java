package pers.mihao.ancient_empire.core.eums;

/**
 * 游戏状态机
 * @version 1.0
 * @auther mihao
 * @date 2020\10\3 0003 15(44
 */
public enum StatusMachineEnum {

    /**
     * 没有选择 初始状态
     */
    NO_CHOOSE,

    /**
     * 选中单位
     */
    CHOOSE_UNIT,

    /**
     * 展示移动区域
     */
    SHOW_MOVE_AREA,

    /**
     * 准备移动
     */
    WILL_MOVE,

    /**
     * 单位移动中
     */
    MOVING,

    /**
     * 移动完毕 还没确定移动
     */
    MOVE_DONE,

    /**
     * 展示可选行动
     */
    SHOW_ACTION,

    /**
     * 即将攻击
     */
    WILL_ATTACH,

    /**
     * 准备召唤
     */
    WILL_SUMMON,

    /**
     * 即将攻击地形
     */
    WILL_ATTACH_REGION,

    /**
     * 攻击中
     */
    ATTACHING,

    /**
     * 召唤中
     */
    SUMMONING,

    /**
     * 攻击结束
     */
    ATTACH_DONE,

    /**
     * 等级提升
     */
    LEVEL_UP,

    /**
     * 必须移动
     */
    MAST_MOVE,

    /**
     * 二次移动
     */
    SECOND_MOVE,

    /**
     * 行动结束
     */
    ACTION_DONE;
}
