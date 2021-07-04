package pers.mihao.ancient_empire.common.log.invoke;

import com.alibaba.fastjson.JSONArray;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.annotation.LogField;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.log.enums.LogFieldEnum;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
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
     * @param log
     */
    @Override
    public final void doPersistLog(AbstractLog log) {
        Runnable runnable = getPersistentTask(log);
        if (log.isAsync()) {
            PersistentLogTaskQueue.getInstance().addTask(runnable);
        } else {
            runnable.run();
        }
    }

    private Runnable getPersistentTask(AbstractLog log) {
        AbstractPersistentLogTask task = getPersistentLogTask();
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
            JSONArray jsonArray;
            for (LogFieldEnum fieldEnum : logFieldEnums) {
                switch (fieldEnum) {
                    case REQUEST:
                        jsonArray = new JSONArray();
                        for (Object obj : log.getArgs()) {
                            jsonArray.add(obj);
                        }
                        map.put(fieldEnum.type(), jsonArray.toJSONString());
                        break;
                    case RESPONSE:
                        jsonArray = new JSONArray();
                        jsonArray.add(log.getRes());
                        map.put(fieldEnum.type(), jsonArray.toJSONString());
                        break;
                    default:
                        map.put(fieldEnum.type(), getValue(log, fieldEnum));
                        break;
                }
            }
        }
    }

    private String getValue(AbstractLog log, LogFieldEnum fieldEnum) {
        Object object = null;
        try {
            Method getterMethod = ReflectUtil.getGetter(fieldEnum.type(), log.getClass());
            if (getterMethod != null) {
                object = getterMethod.invoke(log);
            }
        } catch (Exception e) {
            logger.error("设置属性错误", e);
        }
        if (object != null) {
            return object.toString();
        }
        return null;
    }


    abstract class AbstractPersistentLogTask implements Runnable {

        Object[] args;
        Object res;
        AbstractLog log;

        @Override
        public final void run() {
            if (log == null) {
                logger.error("没有设置要保存的日志");
                return;
            }

            this.res = log.getRes();
            this.args = log.getArgs();
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
