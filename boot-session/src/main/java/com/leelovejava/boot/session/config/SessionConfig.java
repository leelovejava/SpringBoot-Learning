package com.leelovejava.boot.session.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * session配置
 * 设置session的默认在redis中的存活时间
 * @author leelovejava
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 8)
public class SessionConfig {
}
