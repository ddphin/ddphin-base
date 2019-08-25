package com.ddphin.base.common.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AtLeastOneNotNullTarget
 * Function:  AtLeastOneNotNullTarget
 * Date:      2019/6/19 下午3:43
 * Author     ddphin
 * Version    V1.0
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotNullTarget {
    boolean blankable() default false;
}
