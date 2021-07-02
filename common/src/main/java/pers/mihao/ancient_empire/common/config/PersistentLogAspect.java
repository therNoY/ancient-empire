package pers.mihao.ancient_empire.common.config;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.util.CurrUserIdHolder;
import pers.mihao.ancient_empire.common.annotation.PersistentLog;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * 写日志的拦截器
 * @author hspcadmin
 */
@Component
@Aspect
public class PersistentLogAspect {

    Logger log = LoggerFactory.getLogger(PersistentLogAspect.class);

    @Pointcut("@annotation(pers.mihao.ancient_empire.common.annotation.PersistentLog))")
    public void PersistentLog() {
    }

    @Around("PersistentLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method targetMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        PersistentLog persistentLog = targetMethod.getAnnotation(PersistentLog.class);
        if (persistentLog != null) {
            long start = System.currentTimeMillis();
            Object res = joinPoint.proceed();
            long end = System.currentTimeMillis();
            AbstractLog log = ReflectUtil.getNewInstance(persistentLog.bean());
            if (StringUtil.isNotBlack(persistentLog.tableName())) {
                log.setTableName(persistentLog.tableName());
            }
            if (StringUtil.isNotBlack(persistentLog.dataSource())) {
                log.setDataSource(persistentLog.dataSource());
            }
            log.setAsync(persistentLog.isAsync());
            log.setUnderscore(persistentLog.isUnderscore());
            log.setCreateTime(DateUtil.getDataTime());
            log.setExecTime(end - start);
            log.setInvokeMethod(method.getName());
            log.setServiceName(joinPoint.getThis().toString());
            log.setTriggerUserId(CurrUserIdHolder.getUserId());
            ReflectUtil.getSingleton(log.getInvoke()).doPersistLog(joinPoint.getArgs(), res, log);
            return res;
        }else {
            return joinPoint.proceed();
        }
    }
}