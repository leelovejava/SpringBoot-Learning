package com.leelovejava.interview.jvm.oom;

/**
 * 堆栈溢出错误
 * @author leelovejava
 * @date 2019/10/29 23:36
 **/
public class StackOverflowErrorDemo {

    public static void main(String[] args) {
        stackOverflowError();
    }

    private static void stackOverflowError() {
        stackOverflowError();
    }
}
