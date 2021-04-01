package pers.mihao.ancient_empire.core.manger.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.NetConnectTypeEnum;
import pers.mihao.ancient_empire.core.eums.RoomCommendEnum;
import pers.mihao.ancient_empire.core.eums.RoomEventEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.command.RoomCommand;
import pers.mihao.ancient_empire.core.manger.command.StatusCommand;
import pers.mihao.ancient_empire.core.manger.event.AbstractEvent;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.event.RoomEvent;

/**
 * 管理游戏连接统一连接的端点
 *
 * @Author mh32736
 * @Date 2020/9/10 13:26
 */
@Component
@ServerEndpoint("/ae/{type}/{id}/{token}")
public class GameCoreEndpointServer {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static GameSessionManger gameSessionManger;
    private static GameCoreManger gameCoreManger;
    private static UserService userService;
    private static GameRoomService gameRoomService;

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
        gameRoomService = ApplicationContextHolder.getBean(GameRoomService.class);

    }

    /**
     * websocket连接的方法
     *
     * @param session
     * @param type    是单机还是组
     * @param id      游戏的一个gameId
     * @param token   用户的token信息
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
            AbstractSession joinSession = null;
            switch (netConnectType) {
                case STAND_GAME:
                case NET_GAME:
                    if (!gameCoreManger.joinGame(id)) {
                        log.error("加入失败：{}", id);
                        closeSession(session);
                    } else {
                        joinSession = gameSessionManger.addNewGameSession(session, id, connectUser);
                    }
                    break;
                case CREATE_ROOM:
                    GameRoom gameRoom = RedisUtil.getObjectFromJson(BaseConstant.AE_ROOM + id, GameRoom.class);
                    String joinGameCtlArmyColor = null;
                    if (gameRoom != null) {
                        gameRoomService.save(gameRoom);
                        ReqRoomIdDTO reqRoomIdDTO = new ReqRoomIdDTO();
                        reqRoomIdDTO.setRoomId(gameRoom.getRoomId());
                        reqRoomIdDTO.setUserId(Integer.valueOf(userId));
                        joinGameCtlArmyColor = gameRoomService.playerJoinRoom(reqRoomIdDTO);
                    }
                    // 处理创建一定能成功 不能就是有问题
                    if (StringUtil.isNotBlack(joinGameCtlArmyColor)) {
                        RedisUtil.delKey(BaseConstant.AE_ROOM + id);
                        joinSession = gameSessionManger.addNewRoomSession(session, id, connectUser);
                        RoomEvent roomEvent = new RoomEvent(this.id, RoomEventEnum.JOIN_ROOM, connectUser);
                        roomEvent.setArmyColor(joinGameCtlArmyColor);
                        handleRoomEvent(roomEvent);
                    } else {
                        closeSession(session);
                    }
                    break;
                case JOIN_ROOM:
                    ReqRoomIdDTO reqRoomIdDTO = new ReqRoomIdDTO();
                    reqRoomIdDTO.setRoomId(id);
                    reqRoomIdDTO.setUserId(Integer.valueOf(userId));
                    joinGameCtlArmyColor = gameRoomService.playerJoinRoom(reqRoomIdDTO);
                    joinSession = gameSessionManger.addNewRoomSession(session, id, connectUser);
                    if (StringUtil.isNotBlack(joinGameCtlArmyColor)) {
                        RoomEvent roomEvent = new RoomEvent(this.id, RoomEventEnum.JOIN_ROOM, connectUser);
                        roomEvent.setArmyColor(joinGameCtlArmyColor);
                        handleRoomEvent(roomEvent);
                    } else {
                        RoomEvent roomEvent = new RoomEvent(this.id, RoomEventEnum.SEND_MESSAGE, connectUser);
                        roomEvent.setMessage("加入房间");
                        handleRoomEvent(roomEvent);
                    }
                    break;
                case WORLD:
                case FRIEND:
                case SYSTEM:
                default:
                    break;
            }
            if (joinSession != null) {
                sendJoinSessionOk(id, joinSession, netConnectType);
            }
        } catch (Exception e) {
            log.error("", e);
            closeSession(session);
        }
    }

    /**
     * 发送加入成功
     * @param id
     * @param joinSession
     * @param netConnectType
     * @throws IOException
     */
    private void sendJoinSessionOk(String id, AbstractSession joinSession, NetConnectTypeEnum netConnectType) throws IOException {
        StatusCommand command = new StatusCommand();
        command.setOpenId(id);
        switch (netConnectType) {
            case JOIN_ROOM:
                List<ArmyConfig> list = gameRoomService.getCurrentArmyConfigByRoomId(id);
                SerializeConfig config = new SerializeConfig();
                config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                command.setMessage(JSON.toJSONString(list, config));
                break;
            default:
                break;
        }

        joinSession.sendCommand(command);
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
                    GameEvent gameEvent = warpEventMessage(message, GameEvent.class);
                    // 添加到任务队列
                    gameCoreManger.addTask(gameEvent);
                } catch (Exception e) {
                    log.error("", e);
                }
                break;
            case CREATE_ROOM:
            case JOIN_ROOM:
                try {
                    // 校验参数
                    RoomEvent roomEvent = warpEventMessage(message, RoomEvent.class);
                    // 直接处理
                    handleRoomEvent(roomEvent);
                } catch (Exception e) {
                    log.error("", e);
                }
            case SYSTEM:
            case WORLD:
            case FRIEND:
            default:
                break;
        }

    }

    /**
     * 处理ws发来的房间事件消息
     *
     * @param roomEvent
     */
    private void handleRoomEvent(RoomEvent roomEvent) {
        RoomCommand roomCommand = new RoomCommand();
        switch (roomEvent.getEventType()) {
            case SEND_MESSAGE:
                roomCommand.setRoomCommend(RoomCommendEnum.SEND_MESSAGE);
                roomCommand.setMessage("玩家【" + roomEvent.getUser().getName() + "】: " + roomEvent.getMessage());
                break;
            case JOIN_ROOM:
                roomCommand.setJoinArmy(roomEvent.getArmyColor());
                roomCommand.setRoomCommend(RoomCommendEnum.ARMY_CHANGE);
                roomCommand.setMessage("玩家【" + roomEvent.getUser().getName() + "】: " + "加入房间");
                break;
            default:
                break;
        }
        roomCommand.setUserName(roomEvent.getUser().getName());
        roomCommand.setUserId(roomEvent.getUser().getId().toString());
        gameSessionManger.sendMessage2Room(roomCommand, roomEvent.getId());
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
            case CREATE_ROOM:
            case JOIN_ROOM:
                gameSessionManger.userLevelRoom(id, session);
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
    private <T extends AbstractEvent> T warpEventMessage(String message, Class<T> tClass) {
        AbstractEvent event = JSON.parseObject(message, tClass);
        event.setId(id);
        event.setUser(connectUser);
        event.setCreateTime(new Date());
        return (T) event;
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
