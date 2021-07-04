package pers.mihao.ancient_empire.core.manger.command;

import pers.mihao.ancient_empire.core.eums.RoomCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;

/**
 * @Author mihao
 * @Date 2020/9/17 16:26
 */
public class RoomCommand extends AbstractCommand{

    /**
     * 游戏命令类型枚举
     */
    private RoomCommendEnum roomCommend;

    private String userId;

    private String userName;
    /**
     * 需要发送的消息
     */
    private String message;

    /**
     * 开始游戏
     */
    private String recordId;


    /**
     * 加入的军队
     */
    private String joinArmy;

    /**
     * 离开的军队
     */
    private String levelArmy;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public RoomCommand() {
        setSendType(SendTypeEnum.SEND_TO_GROUP);
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

    public String getJoinArmy() {
        return joinArmy;
    }

    public void setJoinArmy(String joinArmy) {
        this.joinArmy = joinArmy;
    }

    public String getLevelArmy() {
        return levelArmy;
    }

    public void setLevelArmy(String levelArmy) {
        this.levelArmy = levelArmy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
