package com.leelovejava.interview.jvm.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * 虚引用 形同虚设
 *
 * ReferenceQueue是用来配合引用工作的,没有ReferenceQueue一样运行.
 *
 * 创建引用的时候可以指定关联的队列,当GC释放对象内存的时候,会将引用加入到引用队列,
 * 如果程序发现某个虚引用已经被加入到引用队列,那么在所引用的对象的内存被回收之前采取必要的行动
 * 这相当于是一种通知机制.
 *
 * 当关联的引用队列中有数据的时候,意味着引用指向的堆内存中的对象被回收。通过这种方式,JVM允许我们在对象被销毁后,
 * 做一些我们自己想做的事情
 * @author leelovejava
 * @date 2019/10/29 23:15
 **/
public class PhantomReferenceDemo {

    /**
     * 必须和引用对象(ReferenceQueue)联合使用
     * 作用： 跟踪对象被回收的状态(在这个对象被垃圾回收的时候收到一个系统通知或者后续动作添加进一步处理)
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        Object o1 = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(o1,referenceQueue);
        System.out.println(o1);
        System.out.println(phantomReference.get());

        System.out.println(referenceQueue.poll());

        System.out.println("*******************");
        o1=null;
        System.gc();
        Thread.sleep(500);

        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());
    }
}
