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
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.core.eums.NetConnectTypeEnum;
import pers.mihao.ancient_empire.core.manger.net.session.AbstractSession;

/**
 * 管理游戏连接统一连接的端点 基础校验,分派
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Component
@ServerEndpoint("/ae/{type}/{id}/{token}")
public class GameCoreEndpointServer {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static RoomSessionManger roomSessionManger;
    private static GameSessionManger gameSessionManger;
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
        roomSessionManger = ApplicationContextHolder.getBean(RoomSessionManger.class);
        gameSessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
        userService = ApplicationContextHolder.getBean(UserService.class);
    }

    /**
     * websocket连接的方法
     *
     * @param session
     * @param type    是单机还是组
     * @param id      Id
     * @param token   用户的token信息
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("type") String type, @PathParam("id") String id,
        @PathParam("token") String token) {
        log.info("有玩家加入 sessionId:{}", session.getId());
        try {
            this.id = id;
            // 首先验证连接信息
            String userId = JwtTokenUtil.getEffectiveUserId(token);
            if (StringUtil.isBlack(userId) || (this.connectUser = userService.getById(userId)) == null) {
                log.error("toke无效或者过期, 禁止连接");
                session.getBasicRemote().sendText(JSONObject.toJSONString(RespUtil.error(40003)));
                closeSession(session);
                return;
            }

            // 分发session到sessionManger
            this.netConnectType = EnumUtil.valueOf(NetConnectTypeEnum.class, type);
            SessionManger sessionManger = getSessionManger();
            // 添加session
            AbstractSession warpSession = sessionManger.addNewSession(session, id, connectUser);
            if (warpSession != null) {
                warpSession.sendCommand(sessionManger.getJoinSuccessCommon(id));
            } else {
                closeSession(session);
            }
        } catch (Exception e) {
            log.error("", e);
            closeSession(session);
        }
    }

    /**
     * 收到客户端消息
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到：{} 发来的消息：{}", connectUser.getName(), message);
        // 校验session状态
        checkSessionStatus(session);
        getSessionManger().handleMessage(message, id, connectUser);
    }




    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("id") String id, Session session) {
        log.info("玩家{}离开：{}", connectUser.getName(), id);
        getSessionManger().removeSession(id ,session);
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

    private void closeSession(Session session) {
        try {
            if (session != null && session.isOpen()) {
                log.warn("关闭session：{}", session.getId());
                session.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 获取sessionManger
     * @return
     */
    private SessionManger getSessionManger(){
        switch (netConnectType) {
            case STAND_GAME:
            case NET_GAME:
            case CHAPTER_GAME:
                return gameSessionManger;
            case CREATE_ROOM:
            case JOIN_ROOM:
                return roomSessionManger;
            case WORLD:
            case FRIEND:
            case SYSTEM:
            default:
                break;
        }
        throw new AeException("不支持的session类型");
    }

}
