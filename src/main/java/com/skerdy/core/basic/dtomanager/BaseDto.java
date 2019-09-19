package com.skerdy.core.basic.dtomanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseDto {

    BaseMethods method() default BaseMethods.INPUT;

    Class<?> serviceClass();

}
