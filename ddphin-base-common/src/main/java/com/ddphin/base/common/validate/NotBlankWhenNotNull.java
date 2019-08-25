package com.ddphin.base.common.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AtLeastOneNotNull
 * Function:  AtLeastOneNotNull
 * Date:      2019/6/19 下午3:43
 * Author     ddphin
 * Version    V1.0
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankWhenNotNullValidator.class )
public @interface NotBlankWhenNotNull {

    String message() default "{javax.validation.constraints.NotBlank.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
