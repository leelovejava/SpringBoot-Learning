package com.leelovejava.interview.jvm.ref;

import java.lang.ref.WeakReference;

/**
 * 弱引用
 * @author leelovejava
 * @date 2019/10/29 22:25
 **/
public class WeakReferenceDemo {
    public static void main(String[] args) {
        Object o1 = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(o1);
        System.out.println(o1);
        System.out.println(weakReference.get());

        o1=null;
        System.out.println("********************");
        System.gc();

        System.out.println(o1);
        System.out.println(weakReference.get());
    }
}
