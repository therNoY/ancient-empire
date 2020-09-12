package pers.mihao.ancient_empire.core.manger;

import java.security.Principal;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import pers.mihao.ancient_empire.common.annotation.Manger;

/**
 * 事件分发器，管理游戏连接的端点
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Manger
@ServerEndpoint("/ae/game/${id}/${token}")
public class EventDispatcherManger {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * websocket连接的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {

    }

}
