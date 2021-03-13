package pers.mihao.ancient_empire.core.eums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 网络连接类型枚举
 * @Author mh32736
 * @Date 2021/3/4 9:29
 */
public enum NetConnectTypeEnum implements BaseEnum {
    /**
     * 单机游戏
     */
    STAND_GAME,
    /**
     * 多人游戏
     */
    NET_GAME,
    /**
     * 房间
     */
    ROOM,
    /**
     * 好友
     */
    FRIEND,
    /**
     * 世界
     */
    WORLD,
    /**
     * 系统
     */
    SYSTEM
}
