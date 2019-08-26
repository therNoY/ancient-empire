package com.mihao.ancient_empire.util.validation.handle;

import com.mihao.ancient_empire.common.enums.BaseEnum;
import com.mihao.ancient_empire.common.vo.MyException;
import com.mihao.ancient_empire.util.validation.InEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InEnumValidator implements ConstraintValidator<InEnum, Object> {
    @Override
    public void initialize(InEnum constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof BaseEnum) {
            throw new MyException("验证类型错误");
        }
        return false;
    }

}
