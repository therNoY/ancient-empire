package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 前端有序命令执行完毕
 * @Author mh32736
 * @Date 2021/4/27 18:40
 */
public class CommendExecOverHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        gameContext.getInteractiveLock().executionOK();
    }
}
