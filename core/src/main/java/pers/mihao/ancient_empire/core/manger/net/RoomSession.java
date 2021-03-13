package pers.mihao.ancient_empire.core.manger.net;

import java.util.Date;
import javax.websocket.Session;
import pers.mihao.ancient_empire.auth.entity.User;

/**
 * @Author mh32736
 * @Date 2021/3/4 18:17
 */
public class RoomSession extends AbstractSession {

    private String roomId;

    public RoomSession() {
    }

    public RoomSession(String roomId, User user, Session session) {
        this.roomId = roomId;
        this.user = user;
        this.session = session;
        this.createDate = new Date();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
