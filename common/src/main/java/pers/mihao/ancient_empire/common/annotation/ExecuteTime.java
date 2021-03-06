package pers.mihao.ancient_empire.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加上该注解表示关心改方法的执行时间
 * @author hspcadmin
 */

// 只能注解到方法上
@Target(ElementType.METHOD)
// 该注解在运行时生效
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExecuteTime {

    /**
     * 默认的最大执行时间 超过会有逻辑操作 单位 s
     * @return
     */
    int maxTime() default 100;

}
