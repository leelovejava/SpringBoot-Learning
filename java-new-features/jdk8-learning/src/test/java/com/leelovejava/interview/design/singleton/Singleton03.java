package com.leelovejava.interview.design.singleton;

/**
 * 线程安全的单例模式： 对 getInstance 方法加锁
 */
public class Singleton03 {
    private Singleton03() {
    }

    private static Singleton03 singleton = null;

    public static synchronized Singleton03 getInstance() {
        if (singleton == null) {
            singleton = new Singleton03();
        }
        return singleton;
    }
}