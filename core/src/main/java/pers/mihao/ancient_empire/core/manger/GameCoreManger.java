package pers.mihao.ancient_empire.core.manger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.util.ThreadPoolNameUtil;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.eums.JoinGameEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;
import pers.mihao.ancient_empire.core.listener.ChapterDialogHelper;
import pers.mihao.ancient_empire.core.listener.GameContextHelperListener;
import pers.mihao.ancient_empire.core.listener.GameRunListener;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.handler.GameHandler;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * 分发事件 处理事件 生成结果 处理结果
 *
 * @Author mihao
 * @Date 2020/9/10 13:37
 */
@Manger
public class GameCoreManger extends AbstractTaskQueueManger<GameEvent> {

    private Logger log = LoggerFactory.getLogger(GameCoreManger.class);
    @Autowired
    GameSessionManger gameSessionManger;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UserTemplateService userTemplateService;

    /* 哨兵的名字 */
    private static final String START_GAME_SENTINEL = "gameContextSentinel";
    /* 哨兵监视等待最大时长 */
    private static final int SENTINEL_TIME = 60;
    /* 加入游戏等待最大时长 */
    private static final int JOIN_TIME = 20;
    /**
     * 是否开发备份数据
     */
    boolean rollBackGame = false;

    /* 初始化注册是事件处理器 */
    private Map<GameEventEnum, Class<GameHandler>> handlerMap = new HashMap<>(GameEventEnum.values().length);

    /**
     * 游戏上下文 Map
     */
    private Map<String, GameContext> contextMap = new ConcurrentHashMap<>(16);

    /**
     * 注册哨兵线程池
     */
    private Executor sentinelPool = new ThreadPoolExecutor(
        0, Integer.MAX_VALUE, 30, TimeUnit.SECONDS,
        new SynchronousQueue(),
        runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(ThreadPoolNameUtil.getThreadName(START_GAME_SENTINEL));
            return thread;
        });


    /**
     * 线程池处理游戏事件任务 支持单个事件事务
     *
     * @param event
     */
    @Override
    public void handelTask(GameEvent event) {
        User user = event.getUser();
        GameContext.setUser(user);
        LoginUserHolder.setLanguage(event.getLanguage());
        GameContext gameContext = contextMap.get(event.getId());
        if (gameContext.isOtherUserEvent() && !event.getEvent().isOtherUserHandle()) {
            // 不是当前回合用户触发的事件 并且不支持其他用户事件处理 不处理
            return;
        }

        // 过滤事件
        if (!event.getEvent().equals(GameEventEnum.COMMEND_EXEC_OVER)
            && gameContext.getStatusMachine().equals(StatusMachineEnum.DIALOG)) {
            // 准备阶段 事件改成处理点击屏幕事件 直接返回
            gameContext.onClickTip();
            return;
        }

        // 备份内存数据
        GameContext cloneContext = null;
        if (rollBackGame) {
            cloneContext = BeanUtil.deptClone(gameContext);
            cloneContext.setGameRunListeners(gameContext.getGameRunListeners());
            cloneContext.setInteractiveLock(gameContext.getInteractiveLock());
        }

        GameCoreHelper.setContext(gameContext);
        try {
            Class clazz = handlerMap.get(event.getEvent());
            if (clazz != null) {
                GameHandler gameHandler = (GameHandler) clazz.newInstance();
                gameHandler.setGameContext(gameContext);
                List<GameCommand> commands = gameHandler.handler(event);
                // 处理任务返回 处理任务结果
                handleCommand(commands, gameContext);
            }
        } catch (Exception e) {
            log.error("执行任务出错: {}, 回退", event, e);
            gameContext = null;
            if (rollBackGame) {
                contextMap.put(event.getId(), cloneContext);
            }
        } finally {
            // 清除三个线程上下文
            LoginUserHolder.clear();
            GameContext.clear();
            GameCoreHelper.removeContext();
        }
    }

    /**
     * 处理命令集合
     *
     * @param commands
     * @param gameId
     */
    public void handleCommand(List<GameCommand> commands, GameContext gameContext) {
        String gameId = gameContext.getGameId();

        if (commands != null && commands.size() > 0) {

            // 过滤掉需要顺序执行的 其他的直接发送
            List<GameCommand> orderCommand = commands.stream().filter(command -> command.getOrder() != null)
                .sorted(Comparator.comparing(Command::getOrder))
                .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(orderCommand)) {
                // 发送了有序命令
                gameContext.getInteractiveLock().executionIng();
                gameSessionManger.sendOrderMessage(orderCommand, gameId);
            }

            for (Command command : commands) {
                if (command.getOrder() == null) {
                    GameCommand gameCommand = (GameCommand) command;
                    gameSessionManger.sendMessage(gameCommand, gameId);
                }
            }
        }
    }

    /**
     * 修改线程池名字
     *
     * @param args
     */
    @Override
    @KnowledgePoint("遍历枚举")
    public void run(String... args) {
        setThreadName("GameEventHandel-");
        // 注册事件对应的处理器
        String packName = GameHandler.class.getPackage().getName();
        String className = Handler.class.getSimpleName();
        String handlerName, classPathName;
        for (GameEventEnum gameEventEnum : GameEventEnum.values()) {
            handlerName = StringUtil.underscoreToHump(gameEventEnum.toString(), true);
            classPathName = packName + CommonConstant.POINT + handlerName + className;
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(classPathName);
                log.info("{} 事件处理注册成功", handlerName);
                handlerMap.put(gameEventEnum, clazz);
            } catch (Exception e) {
                log.error("{} 事件处理注册失败", handlerName);
            }
        }
    }


    /**
     * 异步注册 游戏上下文
     *
     * @param userRecord
     */
    public void registerGameContext(UserRecord userRecord, GameTypeEnum gameType, int playCount) {

        // TODO 检测是否达到最大游戏数量

        if (!contextMap.containsKey(userRecord.getUuid())) {
            sentinelPool.execute(() -> {
                // 设置初始信息
                GameContext gameContext = new GameContext();
                contextMap.put(userRecord.getUuid(), gameContext);

                gameContext.setGameId(userRecord.getUuid());
                gameContext.setGameType(gameType);
                gameContext.setUserRecord(userRecord);
                gameContext.setUserTemplate(userTemplateService.getById(userRecord.getTemplateId()));
                gameContext.setPlayerCount(playCount);

                CyclicBarrier cyclicBarrier = new CyclicBarrier(playCount + 1);
                gameContext.setStartGame(cyclicBarrier);
                UserMap userMap = userMapService.getUserMapById(userRecord.getMapId());

                // 设置监听服务
                List<GameRunListener> listeners = new ArrayList<>();
                GameContextHelperListener coreListener = new GameContextHelperListener();
                coreListener.setGameContext(gameContext);
                listeners.add(coreListener);
                if (gameType.equals(GameTypeEnum.STORY)) {
                    AbstractChapterListener gameRunListener = ChapterDialogHelper.getChapterClass(userMap.getMapName());
                    if (gameRunListener != null) {
                        gameRunListener.setGameContext(gameContext);
                        userRecord.setMaxPop(gameRunListener.getMaxPop());
                        for (Army army : userRecord.getArmyList()) {
                            army.setMoney(gameRunListener.getInitMoney(army));
                        }
                        listeners.add(gameRunListener);
                    }
                }
                gameContext.setGameRunListeners(listeners);

                try {
                    log.info("开始检测上下文：{} 如果没有完成 就会撤销", userRecord.getUuid());
                    cyclicBarrier.await(10, TimeUnit.SECONDS);
                    onGameStart(gameContext);
                    gameContext.setStartGame(null);
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.error("", e);
                } catch (TimeoutException e) {
                    log.error("", e);
                    log.warn("超时没有完成注册 撤销上下文：{}", userRecord.getUuid());
                    doRevokeGameContext(userRecord);
                }
            });
        }
    }


    /**
     * 玩家全部加入可以开始游戏
     *
     * @param userRecord
     */
    private void onGameStart(GameContext gameContext) {
        log.info("玩家全部加入可以开始游戏:{}", gameContext.getGameId());
        gameContext.setStartTime(new Date());
        gameContext.onGameStart();

    }

    /**
     * 撤销上下文
     *
     * @param userRecord
     */
    private void doRevokeGameContext(UserRecord userRecord) {
        contextMap.remove(userRecord.getUuid());
        userRecordService.removeById(userRecord.getUuid());
//        userRecordService.delOtherUnSave(userRecord.getUuid(), userRecord.getCreateUserId());
    }

    /**
     * 玩家加入游戏
     *
     * @param recordId
     */
    public boolean joinGame(String recordId) {
        GameContext gameContext = contextMap.get(recordId);
        if (gameContext != null) {
            CyclicBarrier cyclicBarrier = gameContext.getStartGame();
            if (cyclicBarrier == null) {
                return true;
            }
            try {
                // 等待其他玩家加入
                cyclicBarrier.await(20, TimeUnit.SECONDS);
                return true;
            } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                log.error("", e);
            }
        }
        return false;
    }

    /**
     * 处理所有的人离开
     *
     * @param recordId
     */
    public void allUserLevel(String recordId) {
        GameContext gameContext = contextMap.get(recordId);
        for (GameRunListener listener : gameContext.getGameRunListeners()) {
            if (listener instanceof AbstractGameRunListener) {
                AbstractGameRunListener lockListener = (AbstractGameRunListener) listener;
                lockListener.notifySelf(false);
            }
        }
        contextMap.remove(recordId);
    }

    public GameContext getGameContextById(String uuid) {
        return contextMap.get(uuid);
    }

    public GameContext getOneGame() {
        if (contextMap.size() > 0) {
            return contextMap.entrySet().iterator().next().getValue();
        } else {
            return null;
        }
    }

    public List<GameContext> getAllGameContextList(){
        GameContext[] gameContext = new GameContext[contextMap.size()];
        return Arrays.stream(contextMap.values().toArray(gameContext)).collect(Collectors.toList());
    }

    /**
     * 处理玩家离开record
     * @param userId
     * @param recordId
     */
    public boolean handleUserLevelGame(User user, String recordId) {
        String userId = user.getId().toString();
        // 修改离开的玩家改成机器人操做
        GameContext gameContext = getGameContextById(recordId);
        boolean isPlayer = false;
        // 这里可能会有线程问题 比如这里判断离开的玩家不是当前回合玩家 掉过结束回合 但是此时刚好执行到结束回合所以需要加记录锁
        gameContext.getRecordLock().lock();
        // 本局游戏是否还有连接的玩家 不算观战
        boolean hasActiveUser = true;
        for (Army army : gameContext.getUserRecord().getArmyList()) {
            if (userId.equals(army.getPlayer())) {
                // 后面是机器人操做 玩家断线使用连接符操做
                army.setPlayer(CommonConstant.JOINER + army.getPlayer());
                isPlayer = true;
            }
            if (StringUtil.isNotBlack(army.getPlayer()) && !army.getPlayer().startsWith(CommonConstant.JOINER)) {
                hasActiveUser = hasActiveUser && gameSessionManger.checkConnect(recordId, army.getPlayer());
            }
        }
        if (!hasActiveUser) {
            // 没有玩家全部移除
            gameSessionManger.deleteAllSession(recordId);
        }
        if (userId.equals(gameContext.getUserRecord().getCurrPlayer())) {
            GameEvent roundEndGameEvent = new GameEvent();
            roundEndGameEvent.setEvent(GameEventEnum.ROUND_END);
            roundEndGameEvent.setUser(user);
            roundEndGameEvent.setId(recordId);
            handelTask(roundEndGameEvent);
        }
        gameContext.getRecordLock().unlock();
        return isPlayer;
    }

    /**
     * 判断当前用户是否参与玩家
     * @param userId
     * @param recordId
     */
    public JoinGameEnum joinGame(Integer userId, String recordId) {
        GameContext context = getGameContextById(recordId);
        List<Army> armies = context.getUserRecord().getArmyList();
        for (Army army : armies) {
            if (army.getPlayer() != null) {
                if (army.getPlayer().equals(userId.toString())) {
                    return JoinGameEnum.JOIN;
                } else if (army.getPlayer().equals(CommonConstant.JOINER + userId.toString())) {
                    // 重新设置成玩家
                    army.setPlayer(army.getPlayer().substring(1));
                    return JoinGameEnum.RECONNECT;
                }
            }
        }
        return JoinGameEnum.TOURIST;
    }
}
