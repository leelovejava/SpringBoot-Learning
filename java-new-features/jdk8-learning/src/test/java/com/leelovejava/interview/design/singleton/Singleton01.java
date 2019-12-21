package com.leelovejava.interview.design.singleton;

/**
 * 懒汉模式
 */
public class Singleton01 {
    private Singleton01() {
    }


    private static Singleton01 singleton = null;

    public static Singleton01 getInstance() {
        if (singleton == null) {
            singleton = new Singleton01();
        }
        return singleton;
    }
}