package com.mihao.ancient_empire.config.aspect;

import com.mihao.ancient_empire.common.annotation.ExecuteTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 拦截需要统计方法执行时间的 aop
 */
@Component
@Aspect
public class ExecuteTimeAspect {

    Logger log = LoggerFactory.getLogger(ExecuteTimeAspect.class);

    @Pointcut("@annotation(com.mihao.ancient_empire.common.annotation.ExecuteTime))")
    public void ExecuteTime() {
    }

    @Around("ExecuteTime()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method targetMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        ExecuteTime et = targetMethod.getAnnotation(ExecuteTime.class);
        if (et != null) {
            // 有这个注解 就统计执行时间
            long start = System.currentTimeMillis();
            Object res = joinPoint.proceed();
            long end = System.currentTimeMillis();
            long executeTime = end - start;
            if (et.maxTime() < executeTime) {
                log.error("{} : {} 执行时间: {} 超时 大于最大时间{}",
                        joinPoint.getClass().getName(),
                        method.getName(), executeTime, et.maxTime());
            }else {
                log.info("{} : {} 执行时间: {}", joinPoint.getClass().getName(), method.getName(), executeTime);
            }
            return res;
        }else {
            return joinPoint.proceed();
        }
    }
}
