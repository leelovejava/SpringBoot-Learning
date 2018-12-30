package com.leelovejava.redis.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * redis配置
 *
 * @author tianhao
 * @date 2018/10/27 20:56
 */
@Configuration
public class RedisListenerConfig {
    @Resource
    private RedisConnectionFactory connectionFactory;

    /**
     * 订阅
     *
     * @return
     */
    @Bean
    RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        ///container.addMessageListener(messageListener(), new PatternTopic("__keyevent@0__:expired"));
        return container;
    }

    /*@Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter( new RedisKeyExpirationListener() );
    }*/

}