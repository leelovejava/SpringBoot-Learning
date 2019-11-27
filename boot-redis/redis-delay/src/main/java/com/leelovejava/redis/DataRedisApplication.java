package com.leelovejava.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis 启动类
 *
 * @author leelovejava
 */
@SpringBootApplication
public class DataRedisApplication {


    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DataRedisApplication.class, args);
        StringRedisTemplate redisTemplate2 = ctx.getBean(StringRedisTemplate.class);
        redisTemplate2.opsForValue().set("expire:order:start:1", "1");
        redisTemplate2.expire("expire:order:start:1", 20, TimeUnit.SECONDS);

        redisTemplate2.opsForValue().set("order:start:1", "1");
        redisTemplate2.expire("order:start:1", 30, TimeUnit.SECONDS);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
