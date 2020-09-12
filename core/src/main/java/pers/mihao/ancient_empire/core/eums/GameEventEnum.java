package pers.mihao.ancient_empire.core.eums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 前端反映给后台的游戏内事件枚举
 *
 * @Author mh32736
 * @Date 2020/9/10 16:24
 */
public enum GameEventEnum implements BaseEnum {

    /**
     * 回合开始事件 目前没有指令
     */
    ROUND_START,

    /**
     * 点击可以行动的单位事件
     * 领主如果在己方城堡上会获取行动，通知 SHOW_ACTION
     * 否则通知 SHOW_MOVE_AREA
     */
    CLICK_ACTIVE_UNIT,

    /**
     * 准备攻击事件 获取攻击范围 通知 SHOW_ATTACH_AREA
     */
    ATTACH_UNIT_START,

    /**
     * 选择攻击单位事件 通知移动攻击指针
     */
    CHOOSE_ATTACH_AIM,

    /**
     * 攻击单位 通知 展示攻击状态，改变单位信息  移除单位，添加坟墓
     */
    ATTACH_UNIT,

    /**
     * 购买单位事件, 通知移动指针，添加单位， （如果此处有领主，必须要移动）展示移动范围
     */
    BUY_UNIT,

    /**
     * 单位占领事件 通知修改地形，通知信息条
     */
    REPAIR_BUILD,

    /**
     * 单位修复事件， 通知修改地形，通知信息条
     */
    OCCUPIED_BUILD,

    /**
     * 召唤坟墓事件 通知移除坟墓，添加单位
     */
    SUMMON_TOMB,

    /**
     * 单位结束事件， 改变单位信息，（其他单位加血，该状态）
     */
    UNIT_END,

    /**
     * 准备移动事件, 点击单位移动范围内区域， 展示移动路线命令
     */
    READY_MOVE,

    /**
     * 单位移动 通知单位移动命令
     */
    MOVE,

    /**
     * 撤销移动 修改单位状态
     */
    MOVE_BACK,

    /**
     * 单位移动结束事件， 展示可选行动
     */
    MOVE_END,

    /**
     * 结束回合事件，通知信息条，
     */
    ROUND_END,

}
