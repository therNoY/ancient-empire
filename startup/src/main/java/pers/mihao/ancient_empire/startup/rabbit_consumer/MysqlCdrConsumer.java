package pers.mihao.ancient_empire.startup.rabbit_consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.constant.RabbitMqAdd;

@Component
@RabbitListener(queues = RabbitMqAdd.MYSQL_CDR)
public class MysqlCdrConsumer {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void router() {

    }
}
