package com.ddphin.base.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BindEnum
 *
 * @Date 2019/8/30 上午11:32
 * @Author ddphin
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BindEnum {
    Class value();
}
