package com.leelovejava.interview.design.singleton;

/**
 * 实现方式 3： 静态内部类
 */
public class Singleton05 {
    private static class getInstanceFac {
        private static final Singleton05 INSTANCE = new Singleton05();
    }

    private Singleton05() {
    }

    public static final Singleton05 getInstance() {
        return getInstanceFac.INSTANCE;
    }
}