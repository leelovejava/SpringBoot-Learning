package com.didispace.model;

/**
 * 用户对象
 *
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
public class User {
    private String name;
    private int age;
    private boolean sex;
    private String role;

    public User() {
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * 对象类型属性
     */

    private User friend;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}