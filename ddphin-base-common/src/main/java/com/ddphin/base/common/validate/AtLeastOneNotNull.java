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

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class )
public @interface AtLeastOneNotNull {

    String message() default "At least one getList";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
