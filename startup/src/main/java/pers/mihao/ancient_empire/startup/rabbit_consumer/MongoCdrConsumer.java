package pers.mihao.ancient_empire.startup.rabbit_consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.base.dto.BuyUnitDto;
import pers.mihao.ancient_empire.base.dto.SummonDto;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.mongo.dao.UserRecordRepository;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.common.util.MqMessage;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.core.dto.RespRepairOcpResult;
import pers.mihao.ancient_empire.core.util.UserRecordMongoHelper;

/**
 * mq 消费地址路由的方法
 */
@Component
public class MongoCdrConsumer {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordRepository userRecordRepository;
    @Autowired
    UserRecordMongoHelper userRecordMongoHelper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MqHelper mqHelper;

    public void router() {
        new Thread(() -> {
            for (; ; ) {
                try {
                    MqMessage mqMessage = mqHelper.getMessage();
                    if (mqMessage != null) {
                        log.info("mongo.cdr 消费 at {}", DateUtil.getNow());
                        switch (mqMessage.getMqMethodEnum()) {
                            case ACTION_ATTACH:
                                consumerMes(mqMessage, this::actionAttach);
                                break;
                            case ACTION_REPAIR_OCP:
                                consumerMes(mqMessage, this::actionRepairOcp);
                                break;
                            case ACTION_SUMMON:
                                consumerMes(mqMessage, this::actionSummon);
                                break;
                            case ACTION_END:
                                consumerMes(mqMessage, this::actionEnd);
                                break;
                            case BUY_UNIT:
                                consumerMes(mqMessage, this::buyUnit);
                                break;
                            case END_ROUND:
                                consumerMes(mqMessage, this::endRound);
                                break;
                            default:
                                log.error("没有方法");
                        }
                    } else {
                        log.error("mq 消费信息错误");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 统一处理Mq的消费异常
     *
     * @param mqMessage
     * @param handle
     */
    void consumerMes(MqMessage mqMessage, ConsumerHandle handle) {
        try {
            handle.handel(mqMessage.getValue());
        } catch (Exception e) {
            log.error("mq 信息消费错误", e);
        }
    }

    /**
     * 结束当前回合
     *
     * @param value
     */
    private void endRound(Object value) {
        log.info("mq 处理：endRound");
        UserRecord record = (UserRecord)value;
        // 更新军队坟墓更新当前军队
        userRecordMongoHelper.endRound(record);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + record.getUuid());
    }

    /**
     * 购买单位
     *
     * @param value
     */
    private void buyUnit(Object value) {
        log.info("mq 处理：buyUnit");
        BuyUnitDto buyUnitDto = (BuyUnitDto)value;
        userRecordMongoHelper.handleBuyUnit(buyUnitDto);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + buyUnitDto.getUuid());
    }

    /**
     * 处理占领或者修复的结果
     *
     * @param value
     */
    private void actionRepairOcp(Object value) {
        log.info("mq 处理：actionRepairOcp");
        RespRepairOcpResult repairOcpResult = (RespRepairOcpResult)value;
        userRecordMongoHelper.handleRepairOcp(repairOcpResult);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + repairOcpResult.getRecordId());
    }

    /**
     * 结束行为的影响
     *
     * @param value
     */
    private void actionEnd(Object value) {
        log.info("mq 处理：actionEnd");
        RespEndResultDto endResultDto = (RespEndResultDto)value;
        userRecordMongoHelper.handleEnd(endResultDto);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + endResultDto.getUuid());
    }

    /**
     * 召唤后更新record
     *
     * @param value
     */
    private void actionSummon(Object value) {
        log.info("mq 处理：actionSummon");
        SummonDto summonDto = (SummonDto)value;
        userRecordMongoHelper.handleSummon(summonDto);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + summonDto.getUuid());
    }

    /**
     * 更新军队
     *
     * @param value
     */
    private void actionAttach(Object value) {
        log.info("mq 处理：actionAttach");
        UserRecord record = (UserRecord)value;
        userRecordMongoHelper.updateRecord(record);
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + record.getUuid());
    }

    private interface ConsumerHandle {

        void handel(Object value);
    }
}
