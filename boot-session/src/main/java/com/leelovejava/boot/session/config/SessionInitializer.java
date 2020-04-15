package com.leelovejava.boot.session.config;


import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * 初始化Session配置
 *
 * @author leelovejava
 */
public class SessionInitializer extends AbstractHttpSessionApplicationInitializer {
    public SessionInitializer() {
        super(SessionConfig.class);
    }
}
