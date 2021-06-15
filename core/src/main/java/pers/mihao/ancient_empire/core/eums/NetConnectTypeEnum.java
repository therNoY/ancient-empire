package pers.mihao.ancient_empire.core.eums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * WS连接类型
 * @Author mihao
 * @Date 2021/3/4 9:29
 */
public enum NetConnectTypeEnum implements BaseEnum {
    /**
     * 战役模式
     */
    CHAPTER_GAME,
    /**
     * 遭遇战游戏
     */
    STAND_GAME,
    /**
     * 多人游戏
     */
    NET_GAME,
    /**
     * 创建房间
     */
    CREATE_ROOM,

    /**
     *
     */
    JOIN_ROOM,
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
