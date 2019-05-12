package com.leelovejava.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * jedis的连接池
 */
public class JedisPool {

    private final static Logger logger = LoggerFactory.getLogger(JedisPool.class);

    private static redis.clients.jedis.JedisPool readPool = null;
    private static redis.clients.jedis.JedisPool writePool = null;

    //静态代码初始化池配置
    static {
        try {
            Properties props = new Properties();
            InputStream in = JedisPool.class.getResourceAsStream("/redis.properties");
            props.load(in);

            //创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();

            //设置池配置项值
            config.setMaxTotal(Integer.valueOf(props.getProperty("jedis.pool.maxActive")));
            config.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));
            config.setMaxWaitMillis(Long.valueOf(props.getProperty("jedis.pool.maxWait")));
            config.setTestOnBorrow(Boolean.valueOf(props.getProperty("jedis.pool.testOnBorrow")));
            config.setTestOnReturn(Boolean.valueOf(props.getProperty("jedis.pool.testOnReturn")));

            //根据配置实例化jedis池
            readPool = new redis.clients.jedis.JedisPool(config, props.getProperty("redisReadURL"), Integer.valueOf(props.getProperty("redisReadPort")));
            writePool = new redis.clients.jedis.JedisPool(config, props.getProperty("redisWriteURL"), Integer.valueOf(props.getProperty("redisWritePort")));

        } catch (IOException e) {
            logger.info("redis连接池异常", e);
        }
    }


    /**
     * 获得jedis对象
     */
    public static Jedis getReadJedisObject() {
        return readPool.getResource();
    }

    /**
     * 获得jedis对象
     *
     * @return
     */
    public static Jedis getWriteJedisObject() {
        return writePool.getResource();
    }

    /**
     * 归还jedis对象
     *
     * @param jedis
     */
    public static void returnJedisOjbect(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}