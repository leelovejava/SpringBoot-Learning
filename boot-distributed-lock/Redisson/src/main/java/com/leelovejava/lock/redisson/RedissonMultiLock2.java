package com.leelovejava.lock.redisson;


import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.client.RedisConnectionClosedException;
import org.redisson.client.RedisResponseTimeoutException;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedissonMultiLock的tryLock的精简源码
 * 调用tryLock方法时，事实上调用了RedissonMultiLock的tryLock方法
 *
 * @author tianhao
 * @date 2019/09/09
 * @see org.redisson.RedissonMultiLock
 * @since redisson 2.14.1
 */
public class RedissonMultiLock2 {
    final List<RLock> locks = new ArrayList();
    private final long awaitTime = 1000;
    private final long newLeaseTime = 1000;

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        // 实现要点之允许加锁失败节点限制（N-(N/2+1)）
        int failedLocksLimit = failedLocksLimit();
        List<RLock> acquiredLocks = new ArrayList<RLock>(locks.size());
        // 实现要点之遍历所有节点通过EVAL命令执行lua加锁
        for (ListIterator<RLock> iterator = locks.listIterator(); iterator.hasNext(); ) {
            RLock lock = iterator.next();
            boolean lockAcquired;
            try {
                // 对节点尝试加锁
                lockAcquired = lock.tryLock(awaitTime, newLeaseTime, TimeUnit.MILLISECONDS);
            } catch (RedisConnectionClosedException | RedisResponseTimeoutException e) {
                // 如果抛出这类异常，为了防止加锁成功，但是响应失败，需要解锁
                unlockInner(Arrays.asList(lock));
                lockAcquired = false;
            } catch (Exception e) {
                // 抛出异常表示获取锁失败
                lockAcquired = false;
            }

            if (lockAcquired) {
                // 成功获取锁集合
                acquiredLocks.add(lock);
            } else {
                // 如果达到了允许加锁失败节点限制，那么break，即此次Redlock加锁失败
                if (locks.size() - acquiredLocks.size() == failedLocksLimit()) {
                    break;
                }
            }
        }
        return true;
    }

    private int failedLocksLimit() {
        return 1;
    }

    protected void unlockInner(Collection<RLock> locks) {
        List<RFuture<Void>> futures = new ArrayList(locks.size());
        Iterator var3 = locks.iterator();

        while (var3.hasNext()) {
            RLock lock = (RLock) var3.next();
            futures.add(lock.unlockAsync());
        }

        var3 = futures.iterator();

        while (var3.hasNext()) {
            RFuture<Void> unlockFuture = (RFuture) var3.next();
            unlockFuture.awaitUninterruptibly();
        }

    }
}
