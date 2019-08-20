package com.mihao.ancient_empire.util.validation;

import com.mihao.ancient_empire.util.validation.handle.LengthValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = LengthValidator.class)
public @interface Length {
    int min() default 0;
    int max() default 2147483647;

    String message() default "{参数错误}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

