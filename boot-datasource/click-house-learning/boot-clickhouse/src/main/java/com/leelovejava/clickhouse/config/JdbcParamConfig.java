package com.leelovejava.clickhouse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JDBC参数
 * @author leelovejava
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.click")
public class JdbcParamConfig {
    private String driverClassName ;
    private String url ;
    private Integer initialSize ;
    private Integer maxActive ;
    private Integer minIdle ;
    private Integer maxWait ;
}
