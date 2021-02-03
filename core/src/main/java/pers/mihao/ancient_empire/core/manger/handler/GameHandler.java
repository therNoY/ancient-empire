package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.Handler;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;

import java.util.List;

/**
 * 游戏事件处理类
 * @version 1.0
 * @author mihao
 * @date 20o20\11\8 0008 15:26
 */
public interface GameHandler extends Handler {

    /**
     * 处理事件
     * @param event
     * @return
     */
    List<GameCommand> handler(Event event);

    /**
     * 游戏上下文
     *
     * @param gameContext
     */
    void setGameContext(GameContext gameContext);

}
