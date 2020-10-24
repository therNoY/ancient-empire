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
    NO_CHOOSE( "0"),

    /**
     * 选中单位
     */
    CHOOSE_UNIT( "1"),

    /**
     * 展示移动区域
     */
    SHOW_MOVE_AREA( "2"),

    /**
     * 准备移动
     */
    WILL_MOVE( "3"),

    /**
     * 单位移动中
     */
    MOVING( "4"),

    /**
     * 移动完毕
     */
    MOVE_DONE( "5"),

    /**
     * 展示可选行动
     */
    SHOW_ACTION( "6"),

    /**
     * 即将攻击
     */
    WILL_ATTACH( "7_0"),

    /**
     * 准备召唤
     */
    WILL_SUMMON( "7_1"),

    /**
     * 即将攻击地形
     */
    WILL_ATTACH_REGION( "7_2"),

    /**
     * 攻击中
     */
    ATTACHING( "8_0"),

    /**
     * 召唤中
     */
    SUMMONING( "8_1"),

    /**
     * 攻击结束
     */
    ATTACH_DONE( "9_0"),

    /**
     * 等级提升
     */
    LEVEL_UP( "10"),

    /**
     * 二次移动
     */
    SECOND_MOVE( "11"),

    /**
     * 行动结束
     */
    ACTION_DONE( "12");

    String state;

    StatusMachineEnum(String state) {
        this.state = state;
    }



}
