package com.leelovejava.interview.design.singleton;

/**
 * 恶汉模式
 */
public class Singleton02 {
    private Singleton02() {
    }

    private static Singleton02 singleton = new Singleton02();

    public static Singleton02 getInstance() {
        return singleton;

    }
}