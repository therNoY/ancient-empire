package pers.mihao.ancient_empire.common.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import pers.mihao.ancient_empire.common.annotation.handle.InEnumValidator;
import pers.mihao.ancient_empire.common.enums.BaseEnum;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = InEnumValidator.class)
public @interface InEnum {

    String message() default "{参数不在可选范围内}";

    Class<? extends BaseEnum> in();
}
