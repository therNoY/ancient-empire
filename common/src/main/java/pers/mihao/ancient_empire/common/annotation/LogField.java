package pers.mihao.ancient_empire.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库需要设置的log字段
 *
 * @Author mh32736
 * @Date 2021/6/30 22:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogField {

    /**
     * 数据库对应的字段 省略就是用转下划线
     *
     * @return
     */
    String value() default "";

}
