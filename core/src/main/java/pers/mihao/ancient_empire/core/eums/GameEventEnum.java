package pers.mihao.ancient_empire.core.eums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 前端反映给后台的游戏内事件枚举
 *
 * @Author mihao
 * @Date 2020/9/10 16:24
 */
public enum GameEventEnum implements BaseEnum {

    /**
     * 点击可以行动的单位事件 领主如果在己方城堡上会获取行动，通知 SHOW_ACTION 否则通知 SHOW_MOVE_AREA
     */
    CLICK_ACTIVE_UNIT(true),

    /**
     * 点击不能行动的单位 或者其他单位 改变当前点 当前单位 当前地形
     */
    CLICK_UN_ACTIVE_UNIT(true),

    /**
     * 点击可以行动的单位事件 领主如果在己方城堡上会获取行动，通知 SHOW_ACTION 否则通知 SHOW_MOVE_AREA
     */
    CLICK_REGION(true),

    /**
     * 点击坟墓事件
     */
    CLICK_TOMB(false),

    /**
     * 点击移动区域 准备移动事件 通知 SHOW_MOVE_LINE
     */
    CLICK_MOVE_AREA(false),

    /**
     * 点击指针
     */
    CLICK_POINT(false),

    /**
     * 点击移动的目标点 移动 通知 移动单位 MOVE_UNIT 通知不展示移动区域 DIS_SHOW_MOVE_AREA 并且展示actionSHOW_ACTION
     */
    CLICK_AIM_POINT(false),

    /**
     * 点击选择指针框 可能是攻击单位 召唤单位 治疗单位 给一个单位重新加回合 具体 需要根据状态机判断, 然后通知 攻击/召唤/治疗 动作
     */
    CLICK_CHOOSE_POINT(false),

    /**
     * 准备攻击事件 获取攻击范围 通知 SHOW_ATTACH_AREA
     */
    CLICK_ATTACH_ACTION(false),

    /**
     * 点击移动事件
     */
    CLICK_MOVE_ACTION(false),

    /**
     * 购买单位事件, 通知移动指针，添加单位， （如果此处有领主，必须要移动）展示移动范围
     */
    CLICK_BUY_ACTION(false),

    /**
     * 单位修复事件 通知修改地形，通知信息条
     */
    CLICK_REPAIR_ACTION(false),

    /**
     * 单位占领事件， 通知修改地形，通知信息条
     */
    CLICK_OCCUPIED_ACTION(false),

    /**
     * 召唤坟墓事件 通知移除坟墓，添加单位
     */
    CLICK_SUMMON_ACTION(false),

    /**
     * 单位结束事件， 改变单位信息，（其他单位加血，该状态）
     */
    CLICK_END_ACTION(false),

    /**
     * 单位治疗事件， 改变单位信息，（其他单位加血，该状态）
     */
    CLICK_HEAL_ACTION(false),

    /**
     * 点击攻击地区
     */
    CLICK_ATTACH_AREA(false),

    /**
     * 结束回合事件，通知信息条，
     */
    ROUND_END(false),

    /**
     * 命令执行完毕
     */
    COMMEND_EXEC_OVER(true);

    /**
     * 是否支持非当前回合处理
     */
    boolean otherUserHandle;

    GameEventEnum(boolean otherUserHandle) {
        this.otherUserHandle = otherUserHandle;
    }

    public boolean isOtherUserHandle() {
        return otherUserHandle;
    }
}
