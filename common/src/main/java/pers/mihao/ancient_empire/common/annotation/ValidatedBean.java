package pers.mihao.ancient_empire.common.annotation;

import java.lang.annotation.*;

/**
 * Controller 需要验证bean注解
 * @version 1.0
 * @auther mihao
 * @date 2021\1\17 0017 19:06
 */
@Target({ElementType.TYPE, ElementType.FIELD}) // 只能注解到
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时生效
@Documented
public @interface ValidatedBean {

    /**
     * 非空验证的字段
     */
    String[] notNull() default "";
}
