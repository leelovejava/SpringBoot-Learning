package com.leelovejava.bigdata.phoenix.mybatis.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * Created by jixin on 18-3-11.
 */
@Configuration
@MapperScan(basePackages = PhoenixDataSourceConfig.PACKAGE,
    sqlSessionFactoryRef = "PhoenixSqlSessionFactory")
public class PhoenixDataSourceConfig {

  static final String PACKAGE = "com.imooc.bigdata.phoenix.**";

  @Bean(name = "PhoenixDataSource")
  @Primary
  public DataSource phoenixDataSource() throws IOException {
    ResourceLoader loader = new DefaultResourceLoader();
    InputStream inputStream = loader.getResource("classpath:application.properties")
        .getInputStream();
    Properties properties = new Properties();
    properties.load(inputStream);
    Set<Object> keys = properties.keySet();
    Properties dsProperties = new Properties();
    for (Object key : keys) {
      if (key.toString().startsWith("datasource")) {
        dsProperties.put(key.toString().replace("datasource.", ""), properties.get(key));
      }
    }
    HikariDataSourceFactory factory = new HikariDataSourceFactory();
    factory.setProperties(dsProperties);
    inputStream.close();
    return factory.getDataSource();
  }

  /**
   * Qualifier 注入的方式传入dataSource
   * @param dataSource
   * @return
   * @throws Exception
   */
  @Bean(name = "PhoenixSqlSessionFactory")
  @Primary
  public SqlSessionFactory phoenixSqlSessionFactory(
      @Qualifier("PhoenixDataSource") DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);
    ResourceLoader loader = new DefaultResourceLoader();
    String resource = "classpath:mybatis-config.xml";
    factoryBean.setConfigLocation(loader.getResource(resource));
    factoryBean.setSqlSessionFactoryBuilder(new SqlSessionFactoryBuilder());
    return factoryBean.getObject();
  }
}
