package pers.mihao.ancient_empire.core.manger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Date;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 管理游戏连接的端点
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Component
@ServerEndpoint("/ae/game/${id}/${token}")
public class GameCoreEndpointServer {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GameSessionManger gameSessionManger;

    @Autowired
    GameCoreManger gameCoreManger;

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
            log.error("toke无效或者过期, 禁止连接");
            gameSessionManger.sendMessage(session, RespUtil.error(40003));
            closeSession(session);
        }else {
            gameSessionManger.addNewSession(session, recordId, userId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 校验session状态
        checkSessionStatus(session);
        // 校验参数
        GameEvent gameEvent = warpEventMessage(message);
        // 添加到任务队列
        gameCoreManger.addTask(gameEvent);
    }



    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("id") String recordId, Session session) {
        gameSessionManger.removeSession(recordId, session);
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


    /**
     * 将参数包装成event
     * @param message
     * @return
     */
    private GameEvent warpEventMessage(String message) {
        GameEvent gameEvent = JSON.parseObject(message, GameEvent.class);
        gameEvent.setCreateTime(new Date());
        return gameEvent;
    }

    /**
     * 检查session
     * @param session
     */
    private void checkSessionStatus(Session session) {
        // TODO
    }

    private void closeSession(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
