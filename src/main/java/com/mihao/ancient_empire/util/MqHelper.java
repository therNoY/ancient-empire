package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.common.util.DateUtil;
import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.RabbitMqAdd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq  的工具类 使用时要注入
 */

@Component
public class MqHelper {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 向监听mongoDB cdr 的消费者发送消息
     * @param mqMethodEnum
     * @param object
     */
    public void sendMongoCdr(MqMethodEnum mqMethodEnum, Object object)  {
        MqMessage mqMessage = new MqMessage(mqMethodEnum, object);
        log.info("send to mongo.cdr {} {} at {}",mqMethodEnum.name(), object, DateUtil.getNow());
        amqpTemplate.convertAndSend(RabbitMqAdd.CDR, RabbitMqAdd.MONGO_CDR, JacksonUtil.toJson(mqMessage));
    }
}
