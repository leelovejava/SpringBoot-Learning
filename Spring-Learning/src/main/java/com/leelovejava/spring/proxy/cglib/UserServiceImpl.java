package com.leelovejava.spring.proxy.cglib;

/**
 * 业务类
 * 
 * 没有实现接口
 * 
 * 如果类是final的，则无法生成代理对象，报错
 * 
 * 如果方法是final的，代理无效
 * 
 * @author Muscleape
 *
 */
public class UserServiceImpl {
    public void addUser() {
        System.out.println("增加一个用户。。。");
    }

    public void editUser() {
        System.out.println("编辑一个用户。。。");
    }
}