package com.zhss.demo.zuul.gateway.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时刷新路由任务
 */
@Component
@Configuration
@EnableScheduling
public class RefreshRouteTask {

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private RouteLocator routeLocator;

    @Scheduled(fixedRate = 5000)
    private void refreshRoute() {
        System.out.println("定时刷新路由表");
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
    }

}