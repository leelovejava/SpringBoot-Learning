package com.leelovejava.interview.jvm.oom;

import java.util.Random;

/**
 * @author leelovejava
 * @date 2019/10/31 21:36
 **/
public class JavaHeapSpaceDemo {
    /**
     * -Xms10m -Xmx10m
     * 堆异常
     * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
     * @param args
     */
    public static void main(String[] args) {
        String str = "atguigu";
        while (true) {
            str += str + new Random().nextInt(11111111) + new Random().nextInt(22222222);
            str.intern();
        }
    }
}
