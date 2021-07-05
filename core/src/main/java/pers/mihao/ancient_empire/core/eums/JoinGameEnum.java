package pers.mihao.ancient_empire.core.eums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 玩家加入房间的状态
 * @Author mihao
 * @Date 2021/7/5 17:40
 */
public enum JoinGameEnum implements BaseEnum {
    /**
     * 玩家
     */
    JOIN,

    /**
     * 重新连接
     */
    RECONNECT,

    /**
     * 游客 观战
     */
    TOURIST;

}
