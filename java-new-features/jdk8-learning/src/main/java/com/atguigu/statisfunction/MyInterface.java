package com.atguigu.statisfunction;

/**
 * 6-接口中的默认方法与静态方法
 * @author y.x
 */
public interface MyInterface {
	
	default String getName(){
		return "呵呵呵";
	}
	
	static void show(){
		System.out.println("接口中的静态方法");
	}

}
