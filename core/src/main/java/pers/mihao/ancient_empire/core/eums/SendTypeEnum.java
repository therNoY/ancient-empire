package pers.mihao.ancient_empire.core.eums;

/**
 * 发送命令的模式
 *
 * @Author mihao
 * @Date 2020/9/17 16:27
 */
public enum SendTypeEnum {
    /* 发送给群组的人 */
    SEND_TO_GROUP,

    /* 发送给所有的用户 */
    SEND_TO_USER,

    /* 发送给所有群组 */
    SEND_TO_ALL_GROUP;
}
