package com.mihao.ancient_empire.util.validation.handle;

import com.mihao.ancient_empire.util.validation.Length;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LengthValidator implements ConstraintValidator<Length, CharSequence> {
    private int min;
    private int max;

    public LengthValidator() {
    }

    @Override
    public void initialize(Length parameters) {
        this.min = parameters.min();
        this.max = parameters.max();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        } else {
            int length = value.length();
            return length >= this.min && length <= this.max;
        }
    }
}
