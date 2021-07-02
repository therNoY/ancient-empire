package pers.mihao.ancient_empire.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.log.DefaultLog;

/**
 * 记录日志的注解 和LogField配套使用
 * @Author mh32736
 * @Date 2021/6/30 9:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PersistentLog {

    /**
     * 直接指定表名
     * @return
     */
    String tableName() default "";

    /**
     * 指定数据源
     * @return
     */
    String dataSource() default "";

    /**
     * 记录日志的bean
     * @return
     */
    Class<? extends AbstractLog> bean() default DefaultLog.class;


    /**
     * 默认异步更新
     * @return
     */
    boolean isAsync() default true;

    /**
     * 是否开启下划线
     * @return
     */
    boolean isUnderscore() default true;

    /**
     * 扩展字段展示
     * @return
     */
    String extFieldInfo() default "";

    /**
     * 请求和返回
     * @return
     */
    boolean writeReqAndResp() default false;
}
