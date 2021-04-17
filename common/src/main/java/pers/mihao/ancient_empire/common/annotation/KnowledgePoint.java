package pers.mihao.ancient_empire.common.annotation;


import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 知识点注解 方便找知识点 或者容易出错的地方
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(SOURCE)
@Documented
public @interface KnowledgePoint {

    String value();

    String url() default "";
}
