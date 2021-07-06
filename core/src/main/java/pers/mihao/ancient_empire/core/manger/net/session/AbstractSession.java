package pers.mihao.ancient_empire.core.manger.net.session;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.websocket.Session;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.entity.UserSetting;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.core.manger.command.Command;

/**
 *
 * 基础session
 * @Author mihao
 * @Date 2021/3/4 18:13
 */
public abstract class AbstractSession implements Serializable {

    /**
     * 用户信息
     */
    protected User user;

    /**
     * 用户设置
     */
    protected UserSetting userSetting;

    /**
     * sessionId
     */
    protected String sessionId;

    /**
     * WS session
     */
    protected Session session;

    /**
     * 加入游戏日期
     */
    protected Date createDate;

    /**
     * 离开游戏日期
     */
    protected Date levelDate;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    @KnowledgePoint("session并发高会报错 The remote endpoint was in state [TEXT_FULL_WRITING]")
    public void sendCommand(Command command) throws IOException {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        synchronized (session) {
            session.getBasicRemote().sendText(JSONObject.toJSONString(command, config));
        }
    }

    /**
     * 发送有序命令
     * @param command
     * @throws IOException
     */
    public void sendOrderCommand(List<Command> command) throws IOException {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        synchronized (session) {
            session.getBasicRemote().sendText(JSONArray
                .toJSONString(command, config, SerializerFeature.DisableCircularReferenceDetect));
        }
    }

    public void closeSession() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
