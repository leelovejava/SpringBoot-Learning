package com.leelovejava.lock.redisson;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 普通
 *
 * @author tianhao
 * @date 2019/09/09
 * @since redisson 2.14.1
 */
public class Sample {
    public static void main(String[] args) {
        // 构造redisson实现分布式锁必要的Config
        Config config = new Config();

        // redis之单机模式
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")
                //.setPassword("a123456")
                .setDatabase(0);

        // redis之哨兵模式(sentinel)
       /* config.useSentinelServers().addSentinelAddress(
                "redis://127.0.0.1:26378", "redis://127.0.0.1:26379", "redis://127.0.0.1:26380")
                .setMasterName("mymaster")
                //.setPassword("a123456")
                .setDatabase(0);*/

        // redis之集群模式
        /*config.useClusterServers().addNodeAddress(
                "redis://172.29.3.245:6375", "redis://172.29.3.245:6376", "redis://172.29.3.245:6377",
                "redis://172.29.3.245:6378", "redis://172.29.3.245:6379", "redis://172.29.3.245:6380")
                //.setPassword("a123456")
                .setScanInterval(5000);*/

        // 构造RedissonClient
        RedissonClient redissonClient = Redisson.create(config);
        // 设置锁定资源名称
        RLock disLock = redissonClient.getLock("DISLOCK");
        boolean isLock;
        try {
            //尝试获取分布式锁
            isLock = disLock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
            if (isLock) {
                Thread.sleep(15000);
            }
        } catch (Exception e) {
        } finally {
            // 无论如何, 最后都要解锁
            disLock.unlock();
        }
    }
}
