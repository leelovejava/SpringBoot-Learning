package com.leelovejava.interview.design.singleton;

/**
 * 实现方式 2： 双重检查锁定： 对 getInstance 方法加锁+if 判断
 */
public class Singleton04 {
    private Singleton04() {
    }

    private static Singleton04 singleton = null;

    public static Singleton04 getInstance() {
        if (singleton == null) {
            synchronized (Singleton04.class) {
                if (singleton == null) {
                    singleton = new Singleton04();
                }
            }
            return singleton;
        }
        return singleton;
    }
}