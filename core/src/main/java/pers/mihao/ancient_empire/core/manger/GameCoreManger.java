package pers.mihao.ancient_empire.core.manger;

import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.core.manger.event.Event;

/**
 * 分发事件 处理事件 生成结果 处理结果
 * @Author mh32736
 * @Date 2020/9/10 13:37
 */
@Manger
public class GameCoreManger extends AbstractTaskQueueManger<Event> {


    @Autowired
    GameSessionManger gameSessionManger;


    /**
     * 线程池处理的任务
     * @param event
     */
    @Override
    public void handelTask(Event event) {

    }

    /**
     * 修改线程池名字
     * @param args
     */
    @Override
    public void run(String... args) {
        setThreadName("EventHandel-");
    }


}
