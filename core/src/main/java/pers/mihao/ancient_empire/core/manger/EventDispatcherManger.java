package pers.mihao.ancient_empire.core.manger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.config.ErrorCode;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * 事件分发器，管理游戏连接的端点
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Manger
@ServerEndpoint("/ae/game/${id}/${token}")
public class EventDispatcherManger {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SessionManger sessionManger;

    /**
     * websocket连接的方法
     * @param session
     * @param recordId 游戏的一个gameId
     * @param token 用户的token信息
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String recordId, @PathParam("token") String token) {
        String userId = JwtTokenUtil.getEffectiveUserId(token);
        if (userId == null) {
            log.error("toke无效或者过期");
            sessionManger.sendMessage(session, ErrorCode.getErrorMes(40003));
            closeSession(session);
        }else {
            sessionManger.addNewSession(session, recordId, userId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("id") String recordId, Session session) {
        sessionManger.removeSession(recordId, session);
    }

    /**
     * 未知异常
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void closeSession(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
