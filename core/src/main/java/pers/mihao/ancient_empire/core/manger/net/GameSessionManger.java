package pers.mihao.ancient_empire.core.manger.net;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.net.session.GameSession;

/**
 * 游戏session管理
 * @Author mihao
 * @Date 2021/6/16 9:39
 */
@Manger
public class GameSessionManger extends AbstractSessionManger<GameSession, GameEvent> {

    @Autowired
    private GameCoreManger gameCoreManger;

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
    }

    @Override
    GameSession createSession(Session session, String typeId, User user) {
        // 加入游戏成功才添加到sessionManger
        if (!gameCoreManger.joinGame(typeId)) {
            log.error("加入游戏失败：{}", typeId);
            return null;
        }
        return new GameSession(typeId, user, session, new Date());
    }

    @Override
    void removeSessionFromGroup(GameSession tSession, List<GameSession> lastSession) {
        playerCount.decrementAndGet();
        log.info("玩家:{}从游戏:{}中离开,游戏剩余:{}", tSession.getUser(), tSession.getRecordId(),
            lastSession.size());
    }

    @Override
    void removeGroup(String typeId) {
        gameCoreManger.allUserLevel(typeId);
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
