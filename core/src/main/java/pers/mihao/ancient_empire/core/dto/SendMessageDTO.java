package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

import java.io.Serializable;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\11\25 0025 22:42
 */
public class SendMessageDTO implements Serializable {
    /**
     * 发送的消息
     */
    private String message;

    /**
     * 发送的类型
     */
    private SendTypeEnum sendTypeEnum;

    /**
     * 单独发送的目标
     */
    private String toUser;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SendTypeEnum getSendTypeEnum() {
        return sendTypeEnum;
    }

    public void setSendTypeEnum(SendTypeEnum sendTypeEnum) {
        this.sendTypeEnum = sendTypeEnum;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
