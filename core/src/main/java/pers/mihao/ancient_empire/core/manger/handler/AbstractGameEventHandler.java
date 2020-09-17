package pers.mihao.ancient_empire.core.manger.handler;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 游戏事件处理类 对于每个处理都是一个线程池中的线程执行
 * 执行命令过程中产生command集合 返回给前端
 * @Author mh32736
 * @Date 2020/9/16 18:57
 */
public abstract class AbstractGameEventHandler implements Handler{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    UserRecord userRecord;

    // 命令集合
    List<Command> commandList;

    @Override
    public List<Command> handler(Event event) {
        log.info("开始处理事件：{}", event);
        handlerGameEvent((GameEvent) event);
        log.info("处理事件结束：{} ");
        return commandList;
    }

    /**
     * 返回的命令
     * @param command
     */
    protected void addCommand(Command command) {
        if (commandList == null) {
            commandList = new ArrayList<>();
        }
        commandList.add(command);
    }

    protected GameCommand sendToAllCommand(){
        GameCommand command = new GameCommand();
        command.setSendTypeEnum(SendTypeEnum.SEND_TO_SYSTEM);
        return command;
    }

    protected GameCommand sendToUserCommand(){
        GameCommand command = new GameCommand();
        command.setSendTypeEnum(SendTypeEnum.SEND_TO_USER);
        return command;
    }

    protected GameCommand sendToGameCommand(){
        GameCommand command = new GameCommand();
        command.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
        return command;
    }

    /**
     * 子类据需要实现的处理事件
     * @param gameEvent
     */
    public abstract void handlerGameEvent(GameEvent gameEvent);



}
