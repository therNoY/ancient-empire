package com.mihao.ancient_empire.rabbit_consumer;


import com.mihao.ancient_empire.common.util.DateUtil;
import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.constant.RabbitMqAdd;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dto.mongo_dto.SummonDto;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.dto.ws_dto.RespRepairOcpResult;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.mongo.dao.UserRecordRepository;
import com.mihao.ancient_empire.util.MqMessage;
import com.mihao.ancient_empire.util.UserRecordMongoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq 消费地址路由的方法
 */
@Component
@RabbitListener(queues = RabbitMqAdd.MONGO_CDR)
public class MongoCdrConsumer {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordRepository userRecordRepository;
    @Autowired
    UserRecordMongoHelper userRecordMongoHelper;
    @Autowired
    RedisHelper redisHelper;

    /**
     * 消费者处理消息入口
     * @param object
     */
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
            log.info("mongo.cdr 消费 at {}", DateUtil.getNow());
            switch (mqMessage.getMqMethodEnum()) {
                case ADD_RECORD:
                    addRecord(mqMessage.getValue());
                    break;
                case UPDATE_ARMY:
                    updateArmy(mqMessage.getValue());
                    break;
                case ACTION_REPAIR_OCP:
                    actionRepairOcp(mqMessage.getValue());
                    break;
                case ACTION_SUMMON:
                    actionSummon(mqMessage.getValue());
                    break;
                case ACTION_END:
                    actionEnd(mqMessage.getValue());
                    break;

                default:
                    log.error("没有方法");
            }
        }else {
            log.error("mq 消费信息错误");
        }
    }

    /**
     * 处理占领或者修复的结果
     * @param value
     */
    private void actionRepairOcp(Object value) {
        log.info("mq 处理：actionRepairOcp");
        RespRepairOcpResult repairOcpResult = JacksonUtil.mapToBean(value, RespRepairOcpResult.class);
        userRecordMongoHelper.handleRepairOcp(repairOcpResult);
        redisHelper.delKey(RedisKey.USER_RECORD_ + repairOcpResult.getRecordId());
    }

    /**
     * 结束行为的影响
     * @param value
     */
    private void actionEnd(Object value) {
        log.info("mq 处理：actionEnd");
        RespEndResultDto endResultDto = JacksonUtil.mapToBean(value, RespEndResultDto.class);
        userRecordMongoHelper.handleEnd(endResultDto);
        redisHelper.delKey(RedisKey.USER_RECORD_ + endResultDto.getUuid());
    }

    /**
     * 召唤后更新record
     * @param value
     */
    private void actionSummon(Object value) {
        log.info("mq 处理：actionSummon");
        SummonDto summonDto = JacksonUtil.mapToBean(value, SummonDto.class);
        userRecordMongoHelper.handleSummon(summonDto);
        redisHelper.delKey(RedisKey.USER_RECORD_ + summonDto.getUuid());
    }

    /**
     * 更新军队
     * @param value
     */
    private void updateArmy(Object value) {
        log.info("mq 处理：updateArmy");
        UserRecord userRecord = JacksonUtil.mapToBean(value, UserRecord.class);
        userRecordMongoHelper.updateArmy(userRecord.getUuid(), userRecord.getArmyList());
        redisHelper.delKey(RedisKey.USER_RECORD_ + userRecord.getUuid());
    }

    /**
     * mongo 添加记录
     * @param value
     */
    private void addRecord(Object value) {
        log.info("mq 处理：addRecord");
        UserRecord userRecord = JacksonUtil.mapToBean(value, UserRecord.class);
        userRecordMongoHelper.updateRecord(userRecord.getUuid(), userRecord);
        userRecordRepository.save(userRecord);
    }
}
