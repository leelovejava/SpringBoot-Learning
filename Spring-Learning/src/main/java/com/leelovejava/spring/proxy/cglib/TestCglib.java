package com.leelovejava.spring.proxy.cglib;

/**
 * 测试类
 *
 * @author leelovejava
 */
public class TestCglib {

    /**
     * 引用包cglib-xxx.jar
     * 非Maven项目还需要手动引用包asm-xxx.jar
     * 1. 业务类（不需要定义接口）
     * 2. cglib代理类（实现接口MethodInterceptor）
     * @param args
     */
    public static void main(String[] args) {
        UserServiceCglib cglib = new UserServiceCglib();
        UserServiceImpl bookFacedImpl = (UserServiceImpl) cglib.getInstance(new UserServiceImpl());
        bookFacedImpl.addUser();
    }
}