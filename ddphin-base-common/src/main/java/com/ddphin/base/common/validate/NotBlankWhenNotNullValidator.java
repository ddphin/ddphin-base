package com.ddphin.base.common.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ClassName: NotBlankWhenNotNullValidator
 * Function:  NotBlankWhenNotNullValidator
 * Date:      2019/6/19 下午3:56
 * Author     ddphin
 * Version    V1.0
 */
public class NotBlankWhenNotNullValidator implements ConstraintValidator<NotBlankWhenNotNull,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        else if (value.trim().isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }
}
