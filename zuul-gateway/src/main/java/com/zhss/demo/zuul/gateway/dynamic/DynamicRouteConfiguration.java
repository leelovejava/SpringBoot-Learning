package com.zhss.demo.zuul.gateway.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DynamicRouteConfiguration {

    @Autowired
    private ZuulProperties zuulProperties;
    @Autowired
    private ServerProperties server;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public DynamicRouteLocator routeLocator() {
        DynamicRouteLocator routeLocator = new DynamicRouteLocator(
                this.server.getServletPrefix(), this.zuulProperties);
        routeLocator.setJdbcTemplate(jdbcTemplate);
        return routeLocator;
    }

}