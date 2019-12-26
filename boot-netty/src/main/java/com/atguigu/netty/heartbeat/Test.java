package com.atguigu.netty.heartbeat;

public class Test {
    public static void main(String[] args) throws Exception {
        //纳秒  10亿分之1
        System.out.println(System.nanoTime());
        Thread.sleep(1000);
        System.out.println(System.nanoTime());

    }
}
