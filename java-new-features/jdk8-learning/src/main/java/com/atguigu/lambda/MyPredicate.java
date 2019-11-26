package com.atguigu.lambda;

/**
 * @author y.x
 */
@FunctionalInterface
public interface MyPredicate<T> {

    boolean test(T t);

}
