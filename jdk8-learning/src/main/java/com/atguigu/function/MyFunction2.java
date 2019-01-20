package com.atguigu.function;

/**
 * 函数式接口中使用泛型
 * @param <T>
 * @param <R>
 */
public interface MyFunction2<T, R> {

	 R getValue(T t1, T t2);
	
}
