package pers.mihao.ancient_empire.core.eums;

/**
 * 发送命令的模式
 *
 * @Author mh32736
 * @Date 2020/9/17 16:27
 */
public enum SendTypeEnum {
    /* 发送给整局游戏的人 */
    SEND_TO_GAME,
    /* 发送给用户 */
    SEND_TO_USER,
    /* 发送给在线的人 */
    SEND_TO_SYSTEM
}