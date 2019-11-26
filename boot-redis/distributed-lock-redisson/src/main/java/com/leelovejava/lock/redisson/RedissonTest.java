package com.leelovejava.lock.redisson;

import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author leelovejava
 * @date 2019/11/26 23:08
 * @see 'https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95'
 **/
public class RedissonTest {
    /**
     * 8.1. 可重入锁（Reentrant Lock）
     * 8.2. 公平锁（Fair Lock）
     * 8.3. 联锁（MultiLock）
     * 8.4. 红锁（RedLock）
     * 8.5. 读写锁（ReadWriteLock）
     * 8.6. 信号量（Semaphore）
     * 8.7. 可过期性信号量（PermitExpirableSemaphore）
     * 8.8. 闭锁（CountDownLatch）
     */
    private static RedissonClient redisson;

    static {
        Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL);
        // 超时时间
        ///config.setLockWatchdogTimeout(30);
        config.useClusterServers()
                // use "rediss://" for SSL connection
                .addNodeAddress("perredis://127.0.0.1:7181");
        redisson = Redisson.create(config);
    }

    /**
     * 锁的有效期
     * Redisson内部提供了一个监控锁的看门狗，它的作用是在Redisson实例被关闭前，不断的延长锁的有效期。
     *
     * 1). 默认情况下，看门狗的检查锁的超时时间是30秒钟，也可以通过修改Config.lockWatchdogTimeout来另行指定。
     *
     * 2). 另外Redisson还通过加锁的方法提供了leaseTime的参数来指定加锁的时间。超过这个时间后锁便自动解开了
     * lock.tryLock(100, 10, TimeUnit.SECONDS)
     */

    /**
     * Reentrant Lock
     * 可重入锁
     */
    public void testReentrantLock() throws InterruptedException {
        // 基于Redis的Redisson分布式可重入锁RLock Java对象实现了java.util.concurrent.locks.Lock接口。
        // 同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口
        RLock lock = redisson.getLock("anyLock");
        // Most familiar locking method
        lock.lock();

        // 加锁以后10秒钟自动解锁
        // 无需调用unlock方法手动解锁
        lock.lock(10, TimeUnit.SECONDS);

        // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (res) {
            try {

            } finally {
                lock.unlock();
            }
        }
    }


    /**
     * Reentrant Lock
     * 可重入锁 异步
     */
    public void testReentrantLockAsync() {
        RLock lock = redisson.getLock("anyLock");
        lock.lockAsync();
        lock.lockAsync(10, TimeUnit.SECONDS);
        Future<Boolean> res = lock.tryLockAsync(100, 10, TimeUnit.SECONDS);
    }

    /**
     * 公平锁（Fair Lock）
     * 它保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程。
     * 所有请求线程会在一个队列中排队,当某个线程出现宕机时，Redisson会等待5秒后继续下一个线程，也就是说如果前面有5个线程都处于等待状态，那么后面的线程会等待至少25秒
     */
    public void testFairLock() {
        RLock fairLock = redisson.getFairLock("anyLock");
        // 最常见的使用方法
        fairLock.lock();
    }

    /**
     * 分布式可重入公平锁 异步
     */
    public void testFairLockAsync() {
        RLock fairLock = redisson.getFairLock("anyLock");
        fairLock.lockAsync();
        fairLock.lockAsync(10, TimeUnit.SECONDS);
        Future<Boolean> res = fairLock.tryLockAsync(100, 10, TimeUnit.SECONDS);
    }

    /**
     * 联锁（MultiLock）
     * 基于Redis的Redisson分布式联锁RedissonMultiLock对象可以将多个RLock对象关联为一个联锁，每个RLock对象实例可以来自于不同的Redisson实例
     */
    public void testMultiLock() {
        RLock lock1 = redisson.getLock("lock1");
        RLock lock2 = redisson.getLock("lock2");
        RLock lock3 = redisson.getLock("lock3");

        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
        // 同时加锁：lock1 lock2 lock3
        // 所有的锁都上锁成功才算成功。
        lock.lock();

        lock.unlock();
    }

    /**
     * 联锁（MultiLock）
     * 超时时间设置
     */
    public void testMultiLockExpireTime() throws InterruptedException {
        RLock lock1 = redisson.getLock("lock1");
        RLock lock2 = redisson.getLock("lock2");
        RLock lock3 = redisson.getLock("lock3");
        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
        // 给lock1，lock2，lock3加锁，如果没有手动解开的话，10秒钟后将会自动解开
        lock.lock(10, TimeUnit.SECONDS);

        // 为加锁等待100秒时间，并在加锁成功10秒钟后自动解开
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);

        lock.unlock();
    }

    /**
     * 红锁（RedLock）
     * 将多个RLock对象关联为一个红锁，每个RLock对象实例可以来自于不同的Redisson实例
     */
    public void testRedLock() {

        RLock lock1 = redisson.getLock("lock1");
        RLock lock2 = redisson.getLock("lock2");
        RLock lock3 = redisson.getLock("lock3");

        RedissonRedLock lock = new RedissonRedLock(lock1, lock2, lock3);
        // 同时加锁：lock1 lock2 lock3
        // 红锁在大部分节点上加锁成功就算成功。
        lock.lock();

        lock.unlock();
    }

    /**
     * 红锁（RedLock）过期时间
     *
     * @throws InterruptedException
     */
    public void testRedLockExpireTime() throws InterruptedException {
        RLock lock1 = redisson.getLock("lock1");
        RLock lock2 = redisson.getLock("lock2");
        RLock lock3 = redisson.getLock("lock3");

        RedissonRedLock lock = new RedissonRedLock(lock1, lock2, lock3);
        // 给lock1，lock2，lock3加锁，如果没有手动解开的话，10秒钟后将会自动解开
        lock.lock(10, TimeUnit.SECONDS);

        // 为加锁等待100秒时间，并在加锁成功10秒钟后自动解开
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);

        lock.unlock();
    }

    /**
     * 读写锁（ReadWriteLock）
     * 分布式可重入读写锁允许同时有多个读锁和一个写锁处于加锁状态
     */
    public void testReadWriteLock() {
        RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
        // 最常见的使用方法
        rwlock.readLock().lock();
        // 或
        rwlock.writeLock().lock();
    }

    /**
     * 读写锁（ReadWriteLock）
     * 加锁时间
     */
    public void testReadWriteLockExpireTime() throws InterruptedException {
        RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
        // 10秒钟以后自动解锁
        // 无需调用unlock方法手动解锁
        rwlock.readLock().lock(10, TimeUnit.SECONDS);
        // 或
        rwlock.writeLock().lock(10, TimeUnit.SECONDS);

        // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        boolean res = rwlock.readLock().tryLock(100, 10, TimeUnit.SECONDS);
        // 或
        ///boolean res = rwlock.writeLock().tryLock(100, 10, TimeUnit.SECONDS);

        rwlock.readLock().unlock();
        rwlock.writeLock().unlock();
    }

    /**
     * 信号量（Semaphore）
     * 基于Redis的Redisson的分布式信号量（Semaphore）Java对象RSemaphore采用了与java.util.concurrent.Semaphore相似的接口和用法。
     * 同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口
     * @throws InterruptedException
     */
    public void testSemaphore() throws InterruptedException {
        RSemaphore semaphore = redisson.getSemaphore("semaphore");
        semaphore.acquire();
        //或
        semaphore.acquireAsync();
        semaphore.acquire(23);
        semaphore.tryAcquire();
        //或
        semaphore.tryAcquireAsync();
        semaphore.tryAcquire(23, TimeUnit.SECONDS);
        //或
        semaphore.tryAcquireAsync(23, TimeUnit.SECONDS);
        semaphore.release(10);
        semaphore.release();
        //或
        semaphore.releaseAsync();
    }

    /**
     * 可过期性信号量（PermitExpirableSemaphore）
     * 基于Redis的Redisson可过期性信号量（PermitExpirableSemaphore）是在RSemaphore对象的基础上，为每个信号增加了一个过期时间。
     * 每个信号可以通过独立的ID来辨识，释放时只能通过提交这个ID才能释放。它提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口
     * @throws InterruptedException
     */
    public void testPermitExpirableSemaphore() throws InterruptedException {
        RPermitExpirableSemaphore semaphore = redisson.getPermitExpirableSemaphore("mySemaphore");
        String permitId = semaphore.acquire();
       // 获取一个信号，有效期只有2秒钟。
        String permitId2 = semaphore.acquire(2, TimeUnit.SECONDS);

        semaphore.release(permitId);
    }

    /**
     * 闭锁（CountDownLatch）
     * 基于Redisson的Redisson分布式闭锁（CountDownLatch）Java对象RCountDownLatch采用了与java.util.concurrent.CountDownLatch相似的接口和用法
     * @throws InterruptedException
     */
    public void testCountDownLatch() throws InterruptedException {
        RCountDownLatch latch = redisson.getCountDownLatch("anyCountDownLatch");
        latch.trySetCount(1);
        latch.await();

        // 在其他线程或其他JVM里
        RCountDownLatch latch2 = redisson.getCountDownLatch("anyCountDownLatch");
        latch.countDown();
    }

}
