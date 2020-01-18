package com.leelovejava.redis.test;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

public class RedisDistributedLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 锁的超时时间
     */
    private static int EXPIRE_TIME = 5 * 1000;
    /**
     * 锁等待时间
     */
    private static int WAIT_TIME = 1 * 1000;

    private Jedis jedis;
    private String key;

    public RedisDistributedLock(Jedis jedis, String key) {
        this.jedis = jedis;
        this.key = key;
    }

    /**
     * 不断尝试加锁
     *
     * @return
     */
    public String lock() {
        try {
            // 超过等待时间，加锁失败
            long waitEnd = System.currentTimeMillis() + WAIT_TIME;
            String value = UUID.randomUUID().toString();
            while (System.currentTimeMillis() < waitEnd) {
                ///String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, EXPIRE_TIME);
                String result = jedis.set(key, value);
                jedis.expire(key, EXPIRE_TIME);
                if (LOCK_SUCCESS.equals(result)) {
                    return value;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception ex) {
            System.out.println("lock error " + ex);
        }
        return null;
    }

    public boolean release(String value) {
        if (value == null) {
            return false;
        }
        // 判断key存在并且删除key必须是一个原子操作
        // 且谁拥有锁，谁释放
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = new Object();
        try {
            result = jedis.eval(script, Collections.singletonList(key),
                    Collections.singletonList(value));
            if (RELEASE_SUCCESS.equals(result)) {
                System.out.println("release lock success, value:{}" + value);
                return true;
            }
        } catch (Exception e) {
            System.out.println("release lock error" + e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        System.out.println("release lock failed, value:{}, result:{}" + value + ":" + result);
        return false;
    }
}