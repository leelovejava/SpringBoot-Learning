package com.atguigu.java1.record;

/**
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 15:57
 */
public record Customer(String name, Customer partner) {
    // 还可以声明构造器、静态的变量、静态的方法、实例方法

    public Customer(String name) {
        this(name, null);
    }

    public static String info;

    public static void show() {
        System.out.println("我是一个客户");
    }

    public void showName() {
        System.out.println("我的名字是：" + name());
    }

    //不可以在Record中定义实例变量
//    public int id;

}

//Record不可以声明为abstract的
//abstract record User(){}

//Record不可以显式的继承于其他类
//record User() extends Thread{}
