package com.mihao.ancient_empire.util.validation;

import com.mihao.ancient_empire.common.enums.BaseEnum;
import com.mihao.ancient_empire.util.validation.handle.InEnumValidator;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = InEnumValidator.class)
public @interface InEnum {

    String message() default "{参数不在可选范围内}";

    Class<? extends BaseEnum> in();
}
