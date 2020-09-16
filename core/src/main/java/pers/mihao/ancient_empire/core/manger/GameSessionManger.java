package pers.mihao.ancient_empire.core.manger;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.vo.MyException;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * 管理用户游戏连接的
 * @version 1.0
 * @auther mihao
 * @date 2020\9\14 0014 23:24
 */
@Manger
public class GameSessionManger {

    Logger log = LoggerFactory.getLogger(this.getClass());

    // 保存用户的session
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
        List<GameSession> list = sessionMap.getOrDefault(recordId, new ArrayList<>());
        synchronized (list) {
            GameSession gameSession = new GameSession(recordId, userId, session, new Date());
            gameSession.setSessionId(session.getId());
            list.add(gameSession);
            playerCount.incrementAndGet();
            log.info("将玩家：{} 加入到游戏：{}中, sessionId:{}, 此局游戏目前{}人", userId, recordId, session.getId(), list.size());
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
            throw new MyException("错误的session状态维护 recordId: " + recordId);
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
                    log.info("玩家:{}从游戏:{}中离开,游戏剩余:{}", gameSession.getUserId(), gameSession.getRecordId(),
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
     * @param gameSession
     */
    private void handlePlayerLevel(GameSession gameSession) {
    }


    /**
     * 发送消息
     *
     * @param session
     * @param message
     * @throws IOException
     */
    public void sendMessage(Session session, RespJson message) {
        if (session != null) {
            synchronized (session) {
                log.info("ws  发送数据：" + message);
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
     * @param session
     * @param message
     * @throws IOException
     */
    public void sendMessage2Person(Session session, RespJson message) {
        if (session != null) {
            synchronized (session) {
                log.info("ws  发送数据：" + message);
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    public void sendMessage2Game(Session session, RespJson message) {
        if (session != null) {
            synchronized (session) {
                log.info("ws  发送数据：" + message);
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    public void sendMessage2System(Session session, RespJson message) {
        if (session != null) {
            synchronized (session) {
                log.info("ws  发送数据：" + message);
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
