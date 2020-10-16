package com.atguigu.java;

/**
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 14:27
 */
public class LambdaTest {
    public static void main(String[] args) {
        Runnable r = () -> {
            System.out.println("hello");
        };
    }
}
