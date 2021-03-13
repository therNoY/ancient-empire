package pers.mihao.ancient_empire.core.manger.net;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.event.PlayerJoinRoomEvent;
import pers.mihao.ancient_empire.base.event.PlayerLevelRoomEvent;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

/**
 * 管理用户游戏连接的
 *
 * @version 1.0
 * @author mihao
 * @date 2020\9\14 0014 23:24
 */
@Manger
public class GameSessionManger {

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 保存用户的session key gameId, value 该游戏ID对应的所有GameSession
     */
    private ConcurrentHashMap<String, List<GameSession>> gameSessionMap = new ConcurrentHashMap<>();
    /**
     * 游戏连接的ses
     */
    private ConcurrentHashMap<String, RoomSession> roomSessionMap = new ConcurrentHashMap<>();

    // 记录当前游戏的人数
    private AtomicInteger playerCount = new AtomicInteger(0);

    /**
     * Session 加入队列
     *
     * @param session
     * @param recordId
     * @param user
     */
    public void addNewGameSession(Session session, String recordId, User user) {
        List<GameSession> list = gameSessionMap.get(recordId);
        if (list == null) {
            list = new ArrayList<>();
            gameSessionMap.put(recordId, list);
        }
        synchronized (list) {
            GameSession gameSession = new GameSession(recordId, user, session, new Date());
            gameSession.setSessionId(session.getId());
            list.add(gameSession);
            playerCount.incrementAndGet();
            log.info("将玩家：{} 加入到游戏：{}中, sessionId:{}, 此局游戏目前{}人", user.getName(), recordId, session.getId(), list.size());
        }
    }

    /**
     * 移除队列
     *
     * @param recordId
     * @param session
     */
    public void removeGameSession(String recordId, Session session) {
        List<GameSession> sessionList = gameSessionMap.get(recordId);
        if (sessionList == null) {
            return;
        }
        GameSession gameSession;
        synchronized (sessionList) {
            for (int i = 0; i < sessionList.size(); i++) {
                gameSession = sessionList.get(i);
                if (session.getId().equals(gameSession.getSessionId())) {
                    sessionList.remove(i);
                    playerCount.decrementAndGet();
                    gameSession.setLevelDate(new Date());
                    handlePlayerLevel(gameSession);
                    log.info("玩家:{}从游戏:{}中离开,游戏剩余:{}", gameSession.getUser(), gameSession.getRecordId(),
                            sessionList.size());
                    break;
                }
            }
        }
        if (sessionList.isEmpty()) {
            log.info("游戏：{}, 没有玩家 销毁", recordId);
            gameSessionMap.remove(recordId);
        }

    }

    /**
     * 处理玩家离开游戏服务器
     *
     * @param gameSession
     */
    private void handlePlayerLevel(GameSession gameSession) {
    }


    /**
     * 发送消息
     *
     * @param command
     * @param id
     * @throws IOException
     */
    public void sendMessage(Command command, String id) {
        switch (command.getSendTypeEnum()) {
            case SEND_TO_GAME_USER:
                sendMessage2User(command, id);
                break;
            case SEND_TO_GAME:
                sendMessage2Game(command, id);
                break;
            case SEND_TO_SYSTEM:
                sendMessage2System(command, id);
            case SEND_TO_ROOM:
            default:
                break;
        }
    }

    private void setMessagePrefix(GameCommand command) {
        if (command.getGameCommendEnum().equals(GameCommendEnum.SHOW_GAME_NEWS)) {
            String oldMes = command.getExtMes().getString(ExtMes.MESSAGE);
            if (AuthUtil.getLoginUser() != null) {
                command.getExtMes().put(ExtMes.MESSAGE, "【" + AuthUtil.getLoginUser().getUsername() + "】" + oldMes);
            }else {
                command.getExtMes().put(ExtMes.MESSAGE, "【系统消息】" + oldMes);
            }
        }
    }


    @EventListener
    public void onPlayerJoinRoom(PlayerJoinRoomEvent playerJoinRoomEvent) {
        System.out.println(">>>>>>>>>DemoListener2>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("收到了消息" + playerJoinRoomEvent.toString());
    }

    @EventListener
    public void onPlayerLevelRoom(PlayerLevelRoomEvent playerLevelRoomEvent) {
        System.out.println(">>>>>>>>>DemoListener2>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("收到了消息" + playerLevelRoomEvent.toString());
    }

    /**
     * 发送有序消息集合
     *
     * @param commandList
     * @param gameId
     * @throws IOException
     */
    public void sendOrderMessage2Game(List<? extends Command> commandList, String gameId) {
        List<GameSession> gameSessions = gameSessionMap.get(gameId);
        for (Command command : commandList) {
            setMessagePrefix((GameCommand) command);
        }
        if (gameSessions != null && commandList.size() > 0) {
            GameSession gameSession = null;
            log.info("发送有序命令{} 给群组：{}", commandList, gameId);
            try {
                for (int i = 0; i < gameSessions.size(); i++) {
                    gameSession = gameSessions.get(i);
                    SerializeConfig config = new SerializeConfig();
                    config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                    gameSession.getSession().getBasicRemote().sendText(JSONArray.toJSONString(commandList, config, SerializerFeature.DisableCircularReferenceDetect));
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", gameSession.getUser(), e);
            }


        }
    }

    public String getUserGameId(Integer userId) {
        for (Map.Entry<String, List<GameSession>> entry : gameSessionMap.entrySet()) {
            for (GameSession session : entry.getValue()) {
                if (session.getUserId().equals(userId)) {
                    return entry.getKey();
                }
            }
        }
        throw new AncientEmpireException();
    }

    /**
     * 返回当前游戏的局数
     *
     * @return
     */
    public int getGameCount() {
        return gameSessionMap.size();
    }

    /**
     * 返回当前游戏的总人数
     *
     * @return
     */
    public int getPlayerCount() {
        return playerCount.get();
    }

    /**
     * 发送消息
     *
     * @param command
     * @param gameId
     * @throws IOException
     */
    private void sendMessage2User(Command command, String gameId) {
        GameCommand gameCommand = (GameCommand) command;
        setMessagePrefix(gameCommand);
        List<GameSession> gameSessions = gameSessionMap.get(gameId);
        if (gameSessions != null) {
            GameSession gameSession = null;
            log.info("发送数据{} 给用户：{}", command, gameId);
            try {
                for (int i = 0; i < gameSessions.size(); i++) {
                    gameSession = gameSessions.get(i);
                    if (gameSession.getUser().equals(GameContext.getUser())) {
                        SerializeConfig config = new SerializeConfig();
                        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                        gameSession.getSession().getBasicRemote().sendText(JSONObject.toJSONString(command, config));
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", gameSession.getUser(), e);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param command
     * @param gameId
     * @throws IOException
     */
    public void sendMessage2Game(Command command, String gameId) {
        GameCommand gameCommand = (GameCommand) command;
        setMessagePrefix(gameCommand);
        List<GameSession> gameSessions = gameSessionMap.get(gameId);
        if (gameSessions != null) {
            GameSession gameSession = null;
            log.info("发送数据{} 给群组：{}", command, gameId);
            try {
                for (int i = 0; i < gameSessions.size(); i++) {
                    gameSession = gameSessions.get(i);
                    SerializeConfig config = new SerializeConfig();
                    config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                    gameSession.getSession().getBasicRemote().sendText(JSONObject.toJSONString(command, config));
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", gameSession.getUser(), e);
            }


        }
    }

    /**
     * 发送消息
     *
     * @param command
     * @param gameId
     * @throws IOException
     */
    public void sendMessage2System(Command command, String gameId) {
        for (Map.Entry<String, List<GameSession>> entry : gameSessionMap.entrySet()) {
            List<GameSession> gameSessions = entry.getValue();
            GameSession gameSession = null;
            log.info("发送数据{} 给群组：{}", command, gameId);
            try {
                for (int i = 0; i < gameSessions.size(); i++) {
                    gameSession = gameSessions.get(i);
                    SerializeConfig config = new SerializeConfig();
                    config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                    gameSession.getSession().getBasicRemote().sendText(JSONObject.toJSONString(command, config));
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", gameSession.getUser(), e);
            }
        }
    }

    public void addNewRoomSession(Session session, String id, User connectUser) {
        roomSessionMap.put(id, new RoomSession(id, connectUser, session));
    }

    public void removeRoomSession(String id) {
        roomSessionMap.remove(id);
    }

    public void sendMessage2Room(Command command, String gameId){

    }
}
