package pers.mihao.ancient_empire.core.manger.net;

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
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.NetConnectTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 管理游戏连接统一连接的端点
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@ServerEndpoint("/ae/{type}/{id}/{token}")
public class GameCoreEndpointServer {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static GameSessionManger gameSessionManger;
    private static GameCoreManger gameCoreManger;
    private static UserService userService;

    public GameCoreEndpointServer() {
    }

    /**
     * 连接类型
     */
    private NetConnectTypeEnum netConnectType;
    /**
     * 连接用户
     */
    private User connectUser;

    /**
     * 连接的ID
     */
    private String id;

    static {
        gameSessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
        gameCoreManger = ApplicationContextHolder.getBean(GameCoreManger.class);
        userService = ApplicationContextHolder.getBean(UserService.class);
    }

    /**
     * websocket连接的方法
     *
     * @param session
     * @param type     是单机还是组
     * @param id 游戏的一个gameId
     * @param token    用户的token信息
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("type") String type, @PathParam("id") String id, @PathParam("token") String token) {
        log.info("有玩家加入 sessionId:{}", session.getId());
        try {
            this.id = id;
            String userId = JwtTokenUtil.getEffectiveUserId(token);
            // 首先接受信息
            if (StringUtil.isBlack(userId) || (this.connectUser = userService.getById(userId)) == null) {
                log.error("toke无效或者过期, 禁止连接");
                session.getBasicRemote().sendText(JSONObject.toJSONString(RespUtil.error(40003)));
                closeSession(session);
                return;
            }
            this.netConnectType = EnumUtil.valueOf(NetConnectTypeEnum.class, type);
            boolean joinGameSuccess;
            switch (netConnectType) {
                case STAND_GAME:
                case NET_GAME:
                    gameSessionManger.addNewGameSession(session, id, connectUser);
                    joinGameSuccess = gameCoreManger.joinGame(id);
                    if (!joinGameSuccess) {
                        log.error("加入失败：{}", id);
                        closeSession(session);
                    }
                    break;
                case ROOM:
                    gameSessionManger.addNewRoomSession(session, id, connectUser);
                    break;
                case WORLD:
                case FRIEND:
                case SYSTEM:
                default:
                    break;
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
        log.info("收到：{} 发来的消息：{}", connectUser.getName(), message);
        // 校验session状态
        checkSessionStatus(session);
        switch (netConnectType) {
            case STAND_GAME:
            case NET_GAME:
                try {
                    // 校验参数
                    GameEvent gameEvent = warpGameEventMessage(message);
                    // 添加到任务队列
                    gameCoreManger.addTask(gameEvent);
                } catch (Exception e) {
                    log.error("", e);
                }
                break;
            case ROOM:
            case SYSTEM:
            case WORLD:
            case FRIEND:
            default:
                break;
        }

    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("id") String id, Session session) {
        log.info("玩家{}离开：{}", connectUser.getName(), id);
        switch (netConnectType) {
            case STAND_GAME:
            case NET_GAME:
                gameSessionManger.removeGameSession(id, session);
                break;
            case ROOM:
                gameSessionManger.removeRoomSession(id);
                break;
            case WORLD:
            case FRIEND:
            case SYSTEM:
            default:
                break;
        }
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
    private GameEvent warpGameEventMessage(String message) {
        GameEvent gameEvent = JSON.parseObject(message, GameEvent.class);
        gameEvent.setGameId(id);
        gameEvent.setUser(connectUser);
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
