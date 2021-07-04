package pers.mihao.ancient_empire.core.manger.net.session;

import java.util.Date;
import javax.websocket.Session;
import pers.mihao.ancient_empire.auth.entity.User;

/**
 * 用户连接gameSession
 *
 * @Author mihao
 * @Date 2020/9/15 13:00
 */
public class GameSession extends AbstractSession {

    /**
     * gameId
     */
    private String recordId;

    public GameSession() {
    }

    public GameSession(String recordId, User user, Session session, Date createDate) {
        this.recordId = recordId;
        this.user = user;
        this.session = session;
        this.sessionId = session.getId();
        this.createDate = createDate;
    }


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Integer getUserId() {
        return user.getId();
    }


    @Override
    public String toString() {
        return "GameSession{" +
                "recordId='" + recordId + '\'' +
                ", userName='" + user + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", createDate=" + createDate +
                ", levelDate=" + levelDate +
                '}';
    }
}
