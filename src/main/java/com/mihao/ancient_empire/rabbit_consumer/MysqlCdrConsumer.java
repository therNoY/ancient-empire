package com.mihao.ancient_empire.rabbit_consumer;


import com.mihao.ancient_empire.constant.RabbitMqAdd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMqAdd.MYSQL_CDR)
public class MysqlCdrConsumer {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void router() {

    }
}
