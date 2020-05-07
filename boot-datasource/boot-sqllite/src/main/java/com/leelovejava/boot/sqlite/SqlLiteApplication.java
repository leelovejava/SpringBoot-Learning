package com.leelovejava.boot.sqlite;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动类
 *
 * @author leelovejava
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.leelovejava.boot.sqlite.mapper")
public class SqlLiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlLiteApplication.class, args);
    }
}
