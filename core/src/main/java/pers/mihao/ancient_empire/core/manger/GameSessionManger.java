package pers.mihao.ancient_empire.core.manger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
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

    @Autowired
    UserService userService;

    /**
     * 保存用户的session key gameId, value 该游戏ID对应的所有GameSession
     */
    private ConcurrentHashMap<String, List<GameSession>> sessionMap = new ConcurrentHashMap();

    // 记录当前游戏的人数
    private AtomicInteger playerCount = new AtomicInteger(0);

    /**
     * Session 加入队列
     *
     * @param session
     * @param recordId
     * @param userId
     */
    public void addNewSession(Session session, String recordId, String userId) {
        User user = userService.getById(userId);
        List<GameSession> list = sessionMap.get(recordId);
        if (list == null) {
            list = new ArrayList<>();
            sessionMap.put(recordId, list);
        }
        synchronized (list) {
            GameSession gameSession = new GameSession(recordId, user.getId(), user.getName(), session, new Date());
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
    public void removeSession(String recordId, Session session) {
        List<GameSession> sessionList = sessionMap.get(recordId);
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
                    log.info("玩家:{}从游戏:{}中离开,游戏剩余:{}", gameSession.getUserName(), gameSession.getRecordId(),
                            sessionList.size());
                    break;
                }
            }
        }
        if (sessionList.isEmpty()) {
            log.info("游戏：{}, 没有玩家 销毁", recordId);
            sessionMap.remove(recordId);
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
     * @param gameId
     * @throws IOException
     */
    public void sendMessage(GameCommand command, String gameId) {
        setMessagePrefix(command);
        switch (command.getSendTypeEnum()) {
            case SEND_TO_USER:
                sendMessage2User(command, gameId);
                break;
            case SEND_TO_GAME:
                sendMessage2Game(command, gameId);
                break;
            case SEND_TO_SYSTEM:
                sendMessage2System(command, gameId);
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

    /**
     * 发送有序消息集合
     *
     * @param commandList
     * @param gameId
     * @throws IOException
     */
    public void sendOrderMessage2Game(List<Command> commandList, String gameId) {
        List<GameSession> gameSessions = sessionMap.get(gameId);
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
                log.error("发送数据给用户：{}失败", gameSession.getUserName(), e);
            }


        }
    }

    public String getUserGameId(Integer userId) {
        for (Map.Entry<String, List<GameSession>> entry : sessionMap.entrySet()) {
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
        return sessionMap.size();
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
    private void sendMessage2User(GameCommand command, String gameId) {
        List<GameSession> gameSessions = sessionMap.get(gameId);
        if (gameSessions != null) {
            GameSession gameSession = null;
            log.info("发送数据{} 给用户：{}", command, gameId);
            try {
                for (int i = 0; i < gameSessions.size(); i++) {
                    gameSession = gameSessions.get(i);
                    if (gameSession.getUserName().equals(GameContext.getUserId())) {
                        SerializeConfig config = new SerializeConfig();
                        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                        gameSession.getSession().getBasicRemote().sendText(JSONObject.toJSONString(command, config));
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("发送数据给用户：{}失败", gameSession.getUserName(), e);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param session
     * @param message
     * @throws IOException
     */
    public void sendMessage2Game(GameCommand command, String gameId) {
        List<GameSession> gameSessions = sessionMap.get(gameId);
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
                log.error("发送数据给用户：{}失败", gameSession.getUserName(), e);
            }


        }
    }

    /**
     * 发送消息
     *
     * @param session
     * @param message
     * @throws IOException
     */
    public void sendMessage2System(GameCommand command, String gameId) {
        for (Map.Entry<String, List<GameSession>> entry : sessionMap.entrySet()) {
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
                log.error("发送数据给用户：{}失败", gameSession.getUserName(), e);
            }
        }
    }
}
