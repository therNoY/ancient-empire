package com.mihao.ancient_empire.common.annotation;


import java.lang.annotation.*;

/**
 * 加上该注解表示关心方法的执行时间
 */

@Target(ElementType.METHOD) // 只能注解到方法上
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时生效
@Documented
public @interface ExecuteTime {

    int maxTime() default 300; // 默认的最大执行时间 超过会有逻辑操作 单位 s

}
