package pers.mihao.ancient_empire.common.log.invoke;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.annotation.LogField;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.log.enums.LogFieldEnum;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.util.UidUtil;

/**
 * @Author mh32736
 * @Date 2021/6/30 13:40
 */
public abstract class AbstractPersistentLogInvoke implements PersistentLogInvoke {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 解析参数
     *
     * @param args
     * @param res
     * @param log
     */
    @Override
    public final void doPersistLog(Object[] args, Object res, AbstractLog log) {
        Runnable runnable = getPersistentTask(args, res, log);
        if (log.isAsync()) {
            PersistentLogTaskQueue.getInstance().addTask(runnable);
        } else {
            runnable.run();
        }
    }

    private Runnable getPersistentTask(Object[] args, Object res, AbstractLog log) {
        AbstractPersistentLogTask task = getPersistentLogTask();
        task.args = args;
        task.res = res;
        task.log = log;
        return task;
    }

    /**
     * 从对象中获取日志参数 默认支持LogField注解的字段
     *
     * @param object
     * @return
     */
    protected Map<String, String> getProperByObj(Object object) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>(16);
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            LogField logField = field.getAnnotation(LogField.class);
            if (logField != null) {
                String key = logField.value();
                if (StringUtil.isBlack(key)) {
                    key = field.getName();
                }
                field.setAccessible(true);
                Object valueObj = field.get(object);
                field.setAccessible(false);
                if (valueObj != null) {
                    map.put(key, valueObj.toString());
                }
            }
        }
        return map;
    }

    /**
     * 反射设设置对象的值
     *
     * @param log
     * @param key
     * @param value
     */
    private void getExtProperty(AbstractLog log, Map<String, String> map) {
        LogFieldEnum[] logFieldEnums = log.getExtLogField();
        if (logFieldEnums != null) {
            for (LogFieldEnum fieldEnum : logFieldEnums) {
                switch (fieldEnum) {
                    case LOG_ID:
                        map.put(fieldEnum.type(), log.getLogId());
                        break;
                    case EXEC_TIME:
                        map.put(fieldEnum.type(), log.getExecTime().toString());
                        break;
                    case CREATE_USER_ID:
                        map.put(fieldEnum.type(),
                            log.getTriggerUserId() != null ? log.getTriggerUserId().toString() : null);
                        break;
                    case CREATE_TIME:
                        map.put(fieldEnum.type(), log.getCreateTime());
                        break;
                    case INVOKE_METHOD:
                        map.put(fieldEnum.type(), log.getInvokeMethod());
                        break;
                    case SERVICE_NAME:
                        map.put(fieldEnum.type(), log.getServiceName());
                        break;
                    default:
                        break;
                }
            }
        }
    }


    abstract class AbstractPersistentLogTask implements Runnable {

        Object[] args;
        Object res;
        AbstractLog log;

        @Override
        public final void run() {
            Map<String, String> paramMap = new HashMap<>(16);
            try {
                // 获取返回结果的参数
                if (args != null) {
                    for (Object obj : args) {
                        paramMap.putAll(getProperByObj(obj));
                    }
                }

                // 获取请求中的参数
                if (res != null) {
                    paramMap.putAll(getProperByObj(res));
                }

                // 解析个性化参数
                if (log.getExtendLogParseHandle() != null) {
                    Map<String, Object> parseMap = log.getExtendLogParseHandle().newInstance()
                        .parseLogField(res, args, log);
                    if (parseMap != null) {
                        for (Map.Entry<String, Object> entry : parseMap.entrySet()) {
                            if (entry.getValue() != null) {
                                paramMap.put(entry.getKey(), entry.getValue().toString());
                            }
                        }
                    }
                }

                // 设置logId 默认使用雪花算法
                log.setLogId(UidUtil.getSnowFlakeId().toString());
                // 将参数设置到实体类中
                getExtProperty(log, paramMap);

                doTask(paramMap);
            } catch (Exception e) {
                logger.error("写入日志错误", e);
            }
        }

        /**
         * 真正处理逻辑的方法
         *
         * @param paramMap
         */
        abstract void doTask(Map<String, String> paramMap);
    }


    /**
     * 获取执行任务
     *
     * @return
     */
    protected abstract AbstractPersistentLogTask getPersistentLogTask();
}
