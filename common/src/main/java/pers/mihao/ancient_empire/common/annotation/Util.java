package pers.mihao.ancient_empire.common.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\20 0020 10:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Util {

    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
