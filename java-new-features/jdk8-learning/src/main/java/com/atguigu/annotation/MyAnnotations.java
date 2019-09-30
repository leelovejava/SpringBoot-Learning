package com.atguigu.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD,PARAMETER,CONSTRUCTOR,LOCAL_VARIABLE})
@Retention(RUNTIME)
public @interface MyAnnotations {
    MyAnnotation [] value();
}
