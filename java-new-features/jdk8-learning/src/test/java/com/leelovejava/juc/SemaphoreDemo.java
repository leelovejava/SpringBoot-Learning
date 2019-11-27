package com.leelovejava.juc;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore信号量
 * 用于限制可以访问某些资源（物理or逻辑）的线程数目。
 * 例如，大家排队去银行办理业务，但是只有两个银行窗口提供服务，来了10个人需要办理业务，所以这10个排队的人员需要依次使用这两个业务窗口来办理业务
 *
 * @author leelovejava
 * @see 'https://www.cnblogs.com/itermis/p/9004041.html'
 */
public class SemaphoreDemo {
    /**
     * 1) 观察Semaphore类的基本定义
     * public class Semaphore extends Object implements Serializable
     *
     * 2) 方法
     * public Semaphore(int premits) 设置服务的信号量
     * public Semaphore(int premits，boolean fair) 是否为公平锁
     * public void acquireUninterruptibly(int permits) 等待执行 设置的信号量上如果有阻塞的线程对象存在，那么将一直持续阻塞状态
     * public void release(int permits) 释放线程的阻塞状态
     * public int availablePermits()  返回可用的资源个数
     *
     * 范例：实现银行排队业务办理
     *
     * @param args
     */
    public static void main(String[] args) {
        final Semaphore semaphore = new Semaphore(2); //现在允许操作的资源一共有2个
        final Random random = new Random(); //模拟每一个用户办理业务的时间
        for (int i = 0; i < 10; i++) {
            // 每一个线程就是一个要办理业务的人员
            new Thread(() -> {
                // 现有空余窗口
                if (semaphore.availablePermits() > 0) {
                    System.out.println("[" + Thread.currentThread().getName() + "]进入银行，没有人办理业务");
                } else {
                    // 没有空余位置
                    System.out.println("[" + Thread.currentThread().getName() + "]排队等候办理业务");
                }
                try {
                    // 从信号量中获得操作许可
                    semaphore.acquire();
                    System.out.println("[" + Thread.currentThread().getName() + "]开始办理业务");
                    // 模拟办公延迟
                    TimeUnit.SECONDS.sleep(random.nextInt(10));
                    System.out.println("[" + Thread.currentThread().getName() + "]结束业务办理");
                    // 当前线程离开办公窗口
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "顾客-" + i).start();
        }
    }
}