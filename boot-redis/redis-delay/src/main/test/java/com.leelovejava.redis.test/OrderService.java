package com.leelovejava.redis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

/**
 * 订单
 *
 * @author leelovejava
 * @date 2020/1/18 15:26
 **/
@Component
public class OrderService {
    @Autowired
    private RedisUtil redisUtil;

    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            String tradeKey = "user:" + memberId + ":tradeCode";
            // String tradeCodeFromCache = jedis.get(tradeKey);// 使用lua脚本在发现key的同时将key删除，防止并发订单攻击
            // 对比防重删令牌
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));

            if (eval != null && eval != 0) {
                jedis.del(tradeKey);
                return "success";
            } else {
                return "fail";
            }
        } finally {
            jedis.close();
        }

    }

    public String genTradeCode(String memberId) {

        Jedis jedis = redisUtil.getJedis();

        String tradeKey = "user:" + memberId + ":tradeCode";

        String tradeCode = UUID.randomUUID().toString();
        // 设置过期时间
        jedis.setex(tradeKey, 60 * 15, tradeCode);

        jedis.close();

        return tradeCode;
    }
}
