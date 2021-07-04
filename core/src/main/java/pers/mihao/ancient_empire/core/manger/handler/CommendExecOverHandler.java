package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 前端有序命令执行完毕
 *
 * @Author mihao
 * @Date 2021/4/27 18:40
 */
public class CommendExecOverHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        log.info("收到前端顺序命令交互完毕事件");
        if (gameContext.getInteractiveLock().isExecutionIng()) {
            gameContext.getInteractiveLock().executionOK();
        }
    }
}
