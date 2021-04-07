package pers.mihao.ancient_empire.core.eums;

/**
 * 游戏状态机
 * @version 1.0
 * @author mihao
 * @date 2020\10\3 0003 15(44
 */
public enum StatusMachineEnum {

    /**
     * 对话框阶段 不处理click
     */
    DIALOG,


    /**
     * 初始状态 只有单位被选择 地形被选择
     */
    INIT,

    /**
     * 展示移动区域
     */
    SHOW_MOVE_AREA,


    /**
     * 展示单位移动
     */
    SHOW_MOVE_LINE,

    /**
     * 移动完毕 还没确定移动 可以回退状态
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
     * 在城堡购买单位 必须移动
     */
    MAST_MOVE,

    /**
     * 拥有突袭能力 二次移动
     */
    SECOND_MOVE,

    /**
     * 行动结束
     */
    ACTION_DONE;
}
