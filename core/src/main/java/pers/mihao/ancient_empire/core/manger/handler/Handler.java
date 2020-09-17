package pers.mihao.ancient_empire.core.manger.handler;

import java.util.List;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.event.Event;

/**
 * 事件处理器
 * @Author mh32736
 * @Date 2020/9/16 18:57
 */
public interface Handler {

    /**
     * 游戏上下文
     * @param gameContext
     */
    void setGameContext(GameContext gameContext);

    /**
     * 处理事件
     * @param event
     * @return
     */
    List<Command> handler(Event event);
}
