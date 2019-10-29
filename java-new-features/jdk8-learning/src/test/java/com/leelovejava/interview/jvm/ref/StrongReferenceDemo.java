package com.leelovejava.interview.jvm.ref;

/**
 * 强引用
 * @author leelovejava
 * @date 2019/10/29 21:54
 **/
public class StrongReferenceDemo {
    /**
     * 强引用: 默认,不会被垃圾回收
     * java.lang.ref
     * @param args
     */
    public static void main(String[] args) {
        // 这样定义,默认就是强引用
        Object obj1 = new Object();
        // obj1引用赋值
        Object obj2 = obj1;
        // 置空
        obj1 = null;
        System.gc();
        System.out.println(obj2);
    }
}
