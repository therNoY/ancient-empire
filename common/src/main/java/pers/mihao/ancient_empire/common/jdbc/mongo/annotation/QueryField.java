package pers.mihao.ancient_empire.common.jdbc.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定该字段是否查询
 * @version 1.0
 * @auther mihao
 * @date 2020\9\20 0020 11:08
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface QueryField {

    /* 字段的别名 */
    String value() default "";

    /* 是否查询 */
    boolean query() default true;
}
