package com.leelovejava.spring.proxy.jdk;

/**
 * 业务接口
 *
 * @author leelovejava
 */
public interface UserService {
    /**
     * 增加一个用户
     */
    void addUser();

    /**
     * 编辑账户
     */
    void editUser();
}