package pers.mihao.ancient_empire.core.manger.net.session;

import java.util.Date;
import javax.websocket.Session;
import pers.mihao.ancient_empire.auth.entity.User;

/**
 * @Author mihao
 * @Date 2021/3/4 18:17
 */
public class RoomSession extends AbstractSession {

    private String roomId;

    /**
     * 连接游戏之后控制的军队颜色
     */
    private String joinGameCtlArmyColor;

    /**
     * 是否首个加入组的
     */
    private Boolean isFirstJoinRoom;

    public RoomSession() {
    }

    public RoomSession(String roomId, User user, Session session) {
        this.roomId = roomId;
        this.user = user;
        this.session = session;
        this.sessionId = session.getId();
        this.createDate = new Date();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getJoinGameCtlArmyColor() {
        return joinGameCtlArmyColor;
    }

    public void setJoinGameCtlArmyColor(String joinGameCtlArmyColor) {
        this.joinGameCtlArmyColor = joinGameCtlArmyColor;
    }

    public Boolean getFirstJoinRoom() {
        return isFirstJoinRoom;
    }

    public void setFirstJoinRoom(Boolean firstJoinRoom) {
        isFirstJoinRoom = firstJoinRoom;
    }

    @Override
    public String toString() {
        return "RoomSession{" +
            "roomId='" + roomId + '\'' +
            ", joinGameCtlArmyColor='" + joinGameCtlArmyColor + '\'' +
            ", isFirstJoinRoom=" + isFirstJoinRoom +
            '}';
    }
}
