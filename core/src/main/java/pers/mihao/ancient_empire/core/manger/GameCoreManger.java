package pers.mihao.ancient_empire.core.manger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.listener.AbstractGameRunListener;
import pers.mihao.ancient_empire.core.listener.GameContextHelperListener;
import pers.mihao.ancient_empire.core.listener.ChapterUtil;
import pers.mihao.ancient_empire.core.listener.GameRunListener;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.handler.GameHandler;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.robot.RobotManger;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * 分发事件 处理事件 生成结果 处理结果
 *
 * @Author mh32736
 * @Date 2020/9/10 13:37
 */
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
    @Autowired
    RobotManger robotManger;

    /* 线程池的计数器 */
    private AtomicInteger threadIndex = new AtomicInteger(0);
    /* 哨兵的名字 */
    private static final String START_GAME_SENTINEL = "gameContextSentinel-";
    /* 哨兵监视等待最大时长 */
    private static final int SENTINEL_TIME = 60;
    /* 加入游戏等待最大时长 */
    private static final int JOIN_TIME = 20;

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
                thread.setName(START_GAME_SENTINEL + threadIndex.getAndIncrement());
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
        GameContext gameContext = contextMap.get(event.getId());
        String player = gameContext.getUserRecord().getArmyList().get(gameContext.getUserRecord().getCurrArmyIndex()).getPlayer();
        if (StringUtil.isNotBlack(player) && !player.equals(user.getId().toString())) {
            // 不是当前回合用户触发的事件不处理
            return;
        }


        if (gameContext.getStatusMachine().equals(StatusMachineEnum.DIALOG)) {
            // 准备阶段 事件改成处理点击屏幕事件 直接返回
            gameContext.onClickTip();
            return;
        }

        // 备份内存数据
        GameContext cloneContext = BeanUtil.deptClone(gameContext);
        GameCoreHelper.setContext(gameContext);
        try {
            Class clazz = handlerMap.get(event.getEvent());
            if (clazz != null) {
                GameHandler gameHandler = (GameHandler) clazz.newInstance();
                gameHandler.setGameContext(gameContext);
                List<GameCommand> commands = gameHandler.handler(event);
                // 处理任务返回 处理任务结果
                handleCommand(commands, event.getId());
            }
        } catch (Exception e) {
            log.error("执行任务出错: {}, 回退", event, e);
            gameContext = null;
            contextMap.put(event.getId(), cloneContext);
        } finally {
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
    public void handleCommand(List<GameCommand> commands, String gameId) {

        if (commands != null && commands.size() > 0) {

            // 过滤掉需要顺序执行的 其他的直接发送
            List<GameCommand> orderCommand = commands.stream().filter(command -> command.getOrder() != null)
                    .sorted(Comparator.comparing(Command::getOrder))
                    .collect(Collectors.toList());

            gameSessionManger.sendOrderMessage2Game(orderCommand, gameId);

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
                gameContext.setBgColor(userRecord.getCurrColor());

                CyclicBarrier cyclicBarrier = new CyclicBarrier(playCount + 1);
                gameContext.setStartGame(cyclicBarrier);
                UserMap userMap = userMapService.getUserMapById(userRecord.getMapId());

                // 设置监听服务
                List<GameRunListener> listeners = new ArrayList<>();
                GameContextHelperListener coreListener = new GameContextHelperListener();
                coreListener.setGameContext(gameContext);
                listeners.add(coreListener);
                if (gameType.equals(GameTypeEnum.STORY)) {
                    AbstractChapterListener gameRunListener = ChapterUtil.getChapterClass(userMap.getMapName());
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
                    cyclicBarrier.await(60, TimeUnit.SECONDS);
                    onGameStart(gameContext);
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

    public GameContext getGameSessionById(String uuid) {
        return contextMap.get(uuid);
    }

    public GameContext getOneGame() {
        if (contextMap.size() > 0) {
            return contextMap.entrySet().iterator().next().getValue();
        } else {
            return null;
        }
    }

}
