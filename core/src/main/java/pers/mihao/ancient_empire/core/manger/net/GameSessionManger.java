package pers.mihao.ancient_empire.core.manger.net;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.service.UserSettingService;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.JoinGameEnum;
import pers.mihao.ancient_empire.core.eums.NetConnectTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.net.session.GameSession;
import pers.mihao.ancient_empire.core.util.GameCoreUtil;

/**
 * 游戏session管理
 * @Author mihao
 * @Date 2021/6/16 9:39
 */
@Manger
public class GameSessionManger extends AbstractSessionManger<GameSession, GameEvent> {

    @Autowired
    private GameCoreManger gameCoreManger;

    @Autowired
    UserSettingService userSettingService;

    /**
     * 可以重连的map
     */
    Map<Integer, String> reConnectRecord = new ConcurrentHashMap<>(16);

    Logger log = LoggerFactory.getLogger(GameSessionManger.class);
    /**
     * 记录当前游戏的人数
     */
    private AtomicInteger playerCount = new AtomicInteger(0);

    @Override
    void addNewSession(GameSession session, List<GameSession> sessionList) {
        playerCount.incrementAndGet();
        log.info("将玩家：{} 加入到游戏：{}中, sessionId:{}, 此局游戏目前{}人", session.getUser().getName(), session.getRecordId(), session.getSession().getId(),
            sessionList.size());
        JoinGameEnum joinGame = gameCoreManger.joinGame(session.getUserId(), session.getRecordId());
        GameCommand gameCommand = new GameCommand();
        gameCommand.setGameCommend(GameCommendEnum.SHOW_GAME_NEWS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ExtMes.MESSAGE, GameCoreUtil.getMessage("message." + joinGame.type(), session.getUser().getName()));
        gameCommand.setExtMes(jsonObject);
        sendMessageToGroup(gameCommand, session.getRecordId());
    }

    @Override
    GameSession createSession(Session session, String typeId, User user) {
        // 加入游戏成功才添加到sessionManger
        if (!gameCoreManger.joinGame(typeId)) {
            log.error("加入游戏失败：{}", typeId);
            return null;
        }
        GameSession gameSession = new GameSession(typeId, user, session, new Date());
        gameSession.setUserSetting(userSettingService.getUserSettingById(user.getId()));
        return gameSession;
    }

    @Override
    void beforeCloseByRemove(GameSession gameSession) {
        GameCommand gameCommand = new GameCommand();
        gameCommand.setGameCommend(GameCommendEnum.SHOW_SYSTEM_NEWS);
        JSONObject beCloseTip = new JSONObject();
        beCloseTip.put(ExtMes.MESSAGE, GameCoreUtil.getMessage("tip.allPlayLevel"));
        gameCommand.setExtMes(beCloseTip);
        try {
            gameSession.sendCommand(gameCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void removeSessionFromGroup(GameSession tSession, List<GameSession> lastSession) {
        playerCount.decrementAndGet();
        log.info("玩家:{}从游戏:{}中离开,游戏剩余:{}", tSession.getUser(), tSession.getRecordId(),
            lastSession.size());

        // 给游戏中的玩家发送消息
        GameCommand gameCommand = new GameCommand();
        gameCommand.setGameCommend(GameCommendEnum.SHOW_GAME_NEWS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ExtMes.MESSAGE, GameCoreUtil.getMessage("message.break", tSession.getUser().getName()));
        gameCommand.setExtMes(jsonObject);
        sendMessageToGroup(gameCommand, tSession.getRecordId());
        boolean isPlayer = gameCoreManger.handleUserLevelGame(tSession.getUser(), tSession.getRecordId());
        if (isPlayer && tSession.getConnectType().equals(NetConnectTypeEnum.NET_GAME) && lastSession.size() > 0) {
            reConnectRecord.put(tSession.getUser().getId(), tSession.getRecordId());
        }
    }

    @Override
    void removeGroup(String typeId) {
        gameCoreManger.allUserLevel(typeId);
        reConnectRecord.entrySet().removeIf(entry -> entry.getValue().equals(typeId));
    }

    /**
     * 获取可以重连的记录
     * @param userId
     * @return
     */
    public String getReConnectRecord(Integer userId) {
        return reConnectRecord.get(userId);
    }


    /**
     * 返回当前游戏的总人数
     *
     * @return
     */
    public int getPlayerCount() {
        return playerCount.get();
    }


    @Override
    public void handleEvent(GameEvent gameEvent) {
        // 添加到任务队列
        gameCoreManger.addTask(gameEvent);
    }


}
