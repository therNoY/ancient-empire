package pers.mihao.ancient_empire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * mq  的工具类 使用时要注入
 */

@Component
public class MqHelper {

    Logger log = LoggerFactory.getLogger(this.getClass());


    BlockingQueue<MqMessage> blockingQueue = new LinkedBlockingQueue<>();

    /**
     * 向监听mongoDB cdr 的消费者发送消息
     * @param mqMethodEnum
     * @param object
     */
    public void sendMongoCdr(MqMethodEnum mqMethodEnum, Object object)  {
        MqMessage mqMessage = new MqMessage(mqMethodEnum, object);
        blockingQueue.add(mqMessage);
    }

    public MqMessage getMessage() throws InterruptedException {
        return blockingQueue.take();
    }
}
