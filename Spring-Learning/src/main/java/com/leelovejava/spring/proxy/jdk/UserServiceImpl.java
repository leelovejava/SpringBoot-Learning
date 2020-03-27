package com.leelovejava.spring.proxy.jdk;

/**
 * 业务接口实现类
 *
 * @author leelovejava
 */
public class UserServiceImpl implements UserService {

    @Override
    public void addUser() {
        System.out.println("增加一个用户。。。");
    }

    @Override
    public void editUser() {
        System.out.println("编辑一个用户。。。");
    }

}