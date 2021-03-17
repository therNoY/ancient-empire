package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.core.eums.RoomCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * @Author mh32736
 * @Date 2020/9/17 16:26
 */
public class RoomCommand extends AbstractCommand{

    /**
     * 游戏命令类型枚举
     */
    private RoomCommendEnum roomCommend;

    private String userId;

    private String message;

    public RoomCommand() {
        setSendTypeEnum(SendTypeEnum.SEND_TO_ROOM);
    }

    public RoomCommendEnum getRoomCommend() {
        return roomCommend;
    }

    public void setRoomCommend(RoomCommendEnum roomCommend) {
        this.roomCommend = roomCommend;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
