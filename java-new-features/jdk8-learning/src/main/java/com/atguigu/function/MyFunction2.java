package com.atguigu.function;

/**
 * 函数式接口中使用泛型
 * @param <T> 参数
 * @param <R> 返回值
 */
public interface MyFunction2<T, R> {

	 R getValue(T t1, T t2);
	
}
