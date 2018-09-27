package com.imooc.bigdata.phoenix.mybatis.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.imooc.bigdata.phoenix.mybatis.test.dao.UserInfoMapper;
import com.imooc.bigdata.phoenix.mybatis.test.mybatis.PhoenixDataSourceConfig;

/**
 * Created by jixin on 18-3-11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Import(PhoenixDataSourceConfig.class)
@PropertySource("classpath:application.properties")
@ComponentScan("com.imooc.bigdata.**")
@MapperScan("com.imooc.bigdata.**")
public class BaseTest {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Test
    public void addUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(5);
        userInfo.setName("Jerry");
        userInfoMapper.addUser(userInfo);
    }

    @Test
    public void getUserById() {
        UserInfo userInfo = userInfoMapper.getUserById(1);
        System.out.println(String.format("ID=%s;NAME=%s", userInfo.getId(), userInfo.getName()));
    }

    @Test
    public void getUserByName() {
        UserInfo userInfo = userInfoMapper.getUserByName("Jerry");
        System.out.println(String.format("ID=%s;NAME=%s", userInfo.getId(), userInfo.getName()));
    }

    @Test
    public void deleteUser() {
        userInfoMapper.deleteUser(1);

        List<UserInfo> userInfos = userInfoMapper.getUsers();
        for (UserInfo userInfo : userInfos) {
            System.out.println(String.format("ID=%s;NAME=%s", userInfo.getId(), userInfo.getName()));
        }
    }
}
