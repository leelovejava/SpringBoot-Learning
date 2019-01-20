package com.atguigu.annotation;


import java.lang.reflect.Method;

/**
 * 重复注解与类注解
 */
public class TestAnnotation {
    public void test1() throws NoSuchMethodException {
        Class<TestAnnotation> classz = TestAnnotation.class;
        Method m1 = classz.getMethod("show");
        MyAnnotation[] mas = m1.getAnnotationsByType(MyAnnotation.class);
        for (MyAnnotation myAnnotation : mas
        ) {
            System.out.println(myAnnotation.value());
        }
    }

    @MyAnnotation("hello")
    @MyAnnotation("world")
    public void show(@MyAnnotation("abc") String str){
        //  str :TYPE_PARAMETER
    }
}
