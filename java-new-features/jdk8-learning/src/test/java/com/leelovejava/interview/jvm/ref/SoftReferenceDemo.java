package com.leelovejava.interview.jvm.ref;

import java.lang.ref.SoftReference;

/**
 * 软引用
 * 通常用在对内存敏感的程序中,比如高速缓存就有用到软引用
 * @author leelovejava
 * @date 2019/10/29 22:05
 **/
public class SoftReferenceDemo {

    /**
     * 软引用 内存够用 保留
     */
    public static void softRefMemoryEnough() {
        Object o1 =new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        System.out.println("**********************************");
        o1=null;
        System.gc();

        System.out.println(o1);
        System.out.println(softReference.get());
    }

    /**
     * 软引用 内存不够用 回收
     * JVM配置,估计产生大对象并配置小的内存,让他内存不够用导致OOM,看软引用的回收情况
     * -Xms5m -Xmx5m -XX:+PrintGCDetails
     */
    public static void softRefMemoryNotEnough() {
        Object o1 =new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        try{
            byte [] bytes= new byte[30*1024*1024];
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(o1);
            System.out.println(softReference.get());
        }
    }

    public static void main(String[] args) {
        ///softRefMemoryEnough();

        softRefMemoryNotEnough();
    }
}
