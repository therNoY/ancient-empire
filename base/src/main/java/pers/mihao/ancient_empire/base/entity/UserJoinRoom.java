package pers.mihao.ancient_empire.base.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public class UserJoinRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 房间号
     */
    private String roomId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "UserJoinRoom{" +
        "userId=" + userId +
        ", roomId=" + roomId +
        "}";
    }
}
