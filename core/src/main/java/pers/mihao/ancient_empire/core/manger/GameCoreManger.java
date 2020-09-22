package pers.mihao.ancient_empire.core.manger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.handler.Handler;

/**
 * 分发事件 处理事件 生成结果 处理结果
 *
 * @Author mh32736
 * @Date 2020/9/10 13:37
 */
public class GameCoreManger extends AbstractTaskQueueManger<GameEvent> {

    private Logger log = LoggerFactory.getLogger(GameCoreManger.class);

    /* 初始化注册是事件处理器 */
    Map<GameEventEnum, Handler> handlerMap = new HashMap<>(GameEventEnum.values().length);

    /* 游戏上下文 创建房间 */
    Map<String, GameContext> contextMap = new ConcurrentHashMap<>(16);


    @Autowired
    GameSessionManger gameSessionManger;
    @Autowired
    UserRecordService userRecordService;

    /**
     * 线程池处理的任务
     *
     * @param event
     */
    @Override
    public void handelTask(GameEvent event) {
        GameContext.setUserId(event.getUserId());
        try {
            Handler handler = handlerMap.get(event.getEvent());
            if (handler != null) {
                handler.setGameContext(contextMap.get(event.getGameId()));
                // 处理任务返回 处理任务结果
                List<Command> commands = handler.handler(event);
                for (Command command : commands) {
                    GameCommand gameCommand = (GameCommand) command;
                    gameSessionManger.sendMessage(gameCommand, event.getGameId());
                }
            }
        } catch (Exception e) {
            log.error("执行任务出错:{}", event, e);
        } finally {
            GameContext.clear();
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
        setThreadName("EventHandel-");

        // 注册事件对应的处理器
        String packName = Handler.class.getPackage().getName();
        String className = Handler.class.getSimpleName();
        String handlerName, classPathName;
        Class handlerClass;
        Handler handler;
        for (GameEventEnum gameEventEnum : GameEventEnum.values()) {
            handlerName = StringUtil.underscoreToHump(gameEventEnum.toString(), true);
            classPathName = packName + BaseConstant.POINT + handlerName + className;
            try {
                handlerClass = Class.forName(classPathName);
                handler = (Handler) handlerClass.newInstance();
                log.info("{} 事件处理注册成功", handlerName);
                handlerMap.put(gameEventEnum, handler);
            } catch (Exception e) {
                log.error("{} 事件处理注册失败", handlerName);
            }
        }
    }

    /**
     * 添加 游戏上下文
     * @param recordId
     */
    public void registerGameContext(UserRecord userRecord, GameTypeEnum gameTypeEnum){
        if (!contextMap.containsKey(userRecord.getUuid())) {
            GameContext gameContext = new GameContext();
            gameContext.setGameId(userRecord.getUuid());
            gameContext.setGameTypeEnum(gameTypeEnum);
            gameContext.setUserRecord(userRecord);
            contextMap.put(userRecord.getUuid(), gameContext);
        }
    }

    /**
     * 添加 游戏上下文
     * @param recordId
     */
    public void registerGameContext(String recordId){
        if (!contextMap.containsKey(recordId)) {
            UserRecord userRecord = userRecordService.getRecordById(recordId);
            GameContext gameContext = new GameContext();
            gameContext.setUserRecord(userRecord);
            contextMap.put(recordId, gameContext);
        }
    }


}
