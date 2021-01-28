package pers.mihao.ancient_empire.common.annotation.handle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import pers.mihao.ancient_empire.common.annotation.InEnum;
import pers.mihao.ancient_empire.common.enums.BaseEnum;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

/**
 * 验证字符串是在枚举中
 * @version 1.0
 * @auther mihao
 * @date 2019\8\26 0001 22:21
 */
public class InEnumValidator implements ConstraintValidator<InEnum, Object> {
    @Override
    public void initialize(InEnum constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof BaseEnum) {
            throw new AncientEmpireException("验证类型错误");
        }
        return false;
    }

}
