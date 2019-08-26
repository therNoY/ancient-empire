package com.mihao.ancient_empire.rabbit_consumer;


import com.mihao.ancient_empire.common.util.DateUtil;
import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.constant.RabbitMqAdd;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.mongo.dao.UserRecordRepository;
import com.mihao.ancient_empire.util.MqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMqAdd.MONGO_CDR)
public class MongoCdrConsumer {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordRepository userRecordRepository;

    @RabbitHandler
    public void router(Object object) {
        MqMessage mqMessage = null;
        try {
            Message message = (Message) object;
            String mes = new String(message.getBody(), "utf-8");
            mqMessage = JacksonUtil.jsonToBean(mes, MqMessage.class);
        } catch (Exception e) {
            log.error("", e);
        }
        if (mqMessage != null) {
            log.info("mongo.cdr 消费 {} at {}", mqMessage.getValue(), DateUtil.getNow());
            switch (mqMessage.getMqMethodEnum()) {
                case ADD_RECORD:
                    addRecord(mqMessage.getValue());
                    break;
                default:
                    log.error("没有方法");
            }
        }else {
            log.error("mq 消费信息错误");
        }
    }

    /**
     * mongo 添加记录
     * @param value
     */
    private void addRecord(Object value) {
        UserRecord userRecord = JacksonUtil.mapToBean(value, UserRecord.class);
        userRecordRepository.save(userRecord);
    }
}
