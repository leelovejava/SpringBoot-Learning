package com.atguigu.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


// 重复注解,指定容器类
@Repeatable(MyAnnotations.class)
// 目标
@Target({TYPE, FIELD, METHOD,PARAMETER,CONSTRUCTOR,LOCAL_VARIABLE,TYPE_PARAMETER})
// 生命周期
@Retention(RUNTIME)
public @interface MyAnnotation {
    String value() default "hello world";
}
