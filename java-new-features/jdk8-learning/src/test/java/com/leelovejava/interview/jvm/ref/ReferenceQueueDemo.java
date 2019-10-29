package com.leelovejava.interview.jvm.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 引用队列
 * @author leelovejava
 * @date 2019/10/29 23:06
 **/
public class ReferenceQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        Object o1 = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        WeakReference<Object> weakReference = new WeakReference<>(o1,referenceQueue);

        System.out.println(o1);
        System.out.println(weakReference.get());
        // 消费队列
        System.out.println(referenceQueue.poll());

        System.out.println("*******************");
        o1=null;
        System.gc();
        Thread.sleep(500);

        System.out.println(weakReference.get());
        // 对象被回收之后放到队列中保存一下
        System.out.println(referenceQueue.poll());
    }
}
