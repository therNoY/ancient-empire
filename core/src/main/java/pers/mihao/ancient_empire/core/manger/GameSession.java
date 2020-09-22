package pers.mihao.ancient_empire.core.manger;

import java.util.Date;
import javax.websocket.Session;

/**
 * 用户连接gameSession
 * @Author mh32736
 * @Date 2020/9/15 13:00
 */
public class GameSession {

    /* 游戏id */
    private String recordId;

    /* 用户Id */
    private String userId;

    /* sessionId */
    private String sessionId;

    /* session */
    private Session session;

    /* 加入游戏日期 */
    private Date createDate;

    /* 离开游戏日期 */
    private Date levelDate;

    public GameSession() {
    }

    public GameSession(String recordId, String userId, Session session, Date createDate) {
        this.recordId = recordId;
        this.userId = userId;
        this.session = session;
        this.createDate = createDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLevelDate() {
        return levelDate;
    }

    public void setLevelDate(Date levelDate) {
        this.levelDate = levelDate;
    }

    @Override
    public String toString() {
        return "GameSession{" +
            "recordId='" + recordId + '\'' +
            ", userId='" + userId + '\'' +
            ", sessionId='" + sessionId + '\'' +
            ", createDate=" + createDate +
            ", levelDate=" + levelDate +
            '}';
    }
}
