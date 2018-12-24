package com.leelovejava.bigdata.phoenix.mybatis.config;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by jixin on 18-3-11.
 */
public class HikariDataSourceFactory extends UnpooledDataSourceFactory {

    public HikariDataSourceFactory() {
        this.dataSource = new HikariDataSource();
    }
}
