package com.leelovejava.clickhouse.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * druid配置
 * @author leelovejava
 */
@Configuration
public class DruidConfig {
    @Resource
    private JdbcParamConfig jdbcParamConfig;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcParamConfig.getUrl());
        dataSource.setDriverClassName(jdbcParamConfig.getDriverClassName());
        dataSource.setInitialSize(jdbcParamConfig.getInitialSize());
        dataSource.setMinIdle(jdbcParamConfig.getMinIdle());
        dataSource.setMaxActive(jdbcParamConfig.getMaxActive());
        dataSource.setMaxWait(jdbcParamConfig.getMaxWait());
        return dataSource;
    }
}