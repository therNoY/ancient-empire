package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.manger.Handler;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.event.Event;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\11\8 0008 15:26
 */
public interface GameHandler extends Handler {

    /**
     * 处理事件
     * @param event
     * @return
     */
    List<Command> handler(Event event);

}
