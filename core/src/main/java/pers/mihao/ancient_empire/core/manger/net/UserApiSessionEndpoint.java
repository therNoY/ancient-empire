package pers.mihao.ancient_empire.core.manger.net;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;

/**
 * 通知前端信息的Session 通知好友消息 通知世界聊天 通知房间管理 不收消息
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@ServerEndpoint("/ae/api/{token}")
public class UserApiSessionEndpoint {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Session session;

    private User loginUser;

    private UserApiSessionManger userApiSessionManger;

    public UserApiSessionEndpoint() {
        this.userApiSessionManger = ApplicationContextHolder.getBean(UserApiSessionManger.class);
    }

    /**
     * 用户session连接
     *
     * @param session
     * @param token   用户的token信息
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        UserService userService = ApplicationContextHolder.getBean(UserService.class);
        log.info("有玩家加入 sessionId:{}", session.getId());
        this.session = session;
        try {
            User user = null;
            String userId = JwtTokenUtil.getEffectiveUserId(token);
            if (userId != null) {
                user = userService.getById(userId);
                if (user != null) {
                    this.loginUser = user;
                }
            }

            if (loginUser == null) {
                log.error("toke无效或者过期, 禁止连接");
                session.getBasicRemote().sendText(JSONObject.toJSONString(RespUtil.error(40003)));
                closeSession();
                return;
            }
            log.info("玩家连接成功:{} ", loginUser);
            userApiSessionManger.addNewUserSession(this);
        } catch (Exception e) {
            log.error("", e);
            closeSession();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到：{} 发来的消息：{}", loginUser, message);
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info("玩家{}离开", loginUser);
    }

    /**
     * 未知异常
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 检查session
     *
     * @param session
     */
    private void checkSessionStatus(Session session) {
        // TODO
    }

    public void closeSession() {
        log.warn("关闭session：{}", session.getId());
        try {
            session.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public Session getSession() {
        return session;
    }

    public User getLoginUser() {
        return loginUser;
    }
}
