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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
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
@ServerEndpoint("/ae/{type}/{id}/{token}")
public class GameCoreEndpointServer {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    /* 单机 */
    public static final String STAND = "stand";
    /* 房间进来的路径 */
    public static final String ROOM = "room";

    GameSessionManger gameSessionManger;

    GameCoreManger gameCoreManger;

    private String userId;

    private String recordId;

    @KnowledgePoint(value = "这里只能这样注入", url = "https://www.cnblogs.com/cnsyear/p/12635372.html")
    public GameCoreEndpointServer() {
        this.gameSessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
        this.gameCoreManger = ApplicationContextHolder.getBean(GameCoreManger.class);
    }

    /**
     * websocket连接的方法
     *
     * @param session
     * @param type     是单机还是组
     * @param recordId 游戏的一个gameId
     * @param token    用户的token信息
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("type") String type, @PathParam("id") String recordId, @PathParam("token") String token) {
        log.info("有玩家加入 sessionId:{}", session.getId());
        try {
            String userId = JwtTokenUtil.getEffectiveUserId(token);
            if (userId != null) {
                // 首先接受信息
                this.userId = userId;
                this.recordId = recordId;
                gameSessionManger.addNewSession(session, recordId, userId);
                if (STAND.equals(type)) {
                    gameCoreManger.registerGameContext(recordId);
                }else if (ROOM.equals(type)){
                    // TODO room连接
                }else {
                    closeSession(session);
                }
            } else {
                log.error("toke无效或者过期, 禁止连接");
                session.getBasicRemote().sendText(JSONObject.toJSONString(RespUtil.error(40003)));
                closeSession(session);
            }
        } catch (Exception e) {
            log.error("", e);
            closeSession(session);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到：{} 发来的消息：{}", userId, message);
        try {
            // 校验session状态
            checkSessionStatus(session);
            // 校验参数
            GameEvent gameEvent = warpEventMessage(message);
            // 添加到任务队列
            gameCoreManger.addTask(gameEvent);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("id") String recordId, Session session) {
        log.info("玩家{}离开：{}", userId, recordId);
        gameSessionManger.removeSession(recordId, session);
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
     * 将参数包装成event
     *
     * @param message
     * @return
     */
    private GameEvent warpEventMessage(String message) {
        GameEvent gameEvent = JSON.parseObject(message, GameEvent.class);
        gameEvent.setGameId(recordId);
        gameEvent.setUserId(recordId);
        gameEvent.setCreateTime(new Date());
        return gameEvent;
    }

    /**
     * 检查session
     *
     * @param session
     */
    private void checkSessionStatus(Session session) {
        // TODO
    }

    private void closeSession(Session session) {
        log.warn("关闭session：{}", session.getId());
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
