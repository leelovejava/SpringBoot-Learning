package com.atguigu.statisfunction;

public interface MyFun {
	/**
	 * 接口中默认方法
	 * @return
	 */
	default String getName(){
		return "哈哈哈";
	}

}
