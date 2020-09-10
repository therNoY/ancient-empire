package pers.mihao.ancient_empire.core.manger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.core.manger.event.Event;

/**
 * @Author mh32736
 * @Date 2020/9/10 13:37
 */
@Manger
public class GameCoreManger {

    BlockingQueue<Event> eventQueue = new LinkedTransferQueue<>();

    public void handelEvent() throws InterruptedException {
        for (;;) {
            Event event = eventQueue.take();
        }
    }

}
