package com.leelovejava.clickhouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author leelovejava
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.leelovejava.clickhouse.mapper"})
public class ClickHouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClickHouseApplication.class, args);
    }
}