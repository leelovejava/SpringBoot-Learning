package com.leelovejava.bigdata.phoenix.mybatis;

import java.util.List;

import com.leelovejava.bigdata.phoenix.mybatis.config.PhoenixDataSourceConfig;
import com.leelovejava.bigdata.phoenix.mybatis.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.leelovejava.bigdata.phoenix.mybatis.dao.UserInfoMapper;

/**
 * Created by jixin on 18-3-11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Import(PhoenixDataSourceConfig.class)
@PropertySource("classpath:application.properties")
@ComponentScan("com.imooc.bigdata.phoenix.**")
@MapperScan("com.leelovejava.bigdata.phoenix.mybatis.dao.**")
public class BaseTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 错误:
     * java.lang.NoSuchMethodError: org.apache.hadoop.security.authentication.util.KerberosUtil.hasKerberosKeyTab(Ljavax/security/auth/Subject;)Z
     * http://www.cnblogs.com/sweetchildomine/p/9690106.html
     * 引用最新的版本,导致不兼容
     * <p>
     * 创建表
     * create table if not exists USER_INFO(ID INTEGER NOT NULL PRIMARY KEY,NAME VARCHAR(20));
     */
    @Test
    public void addUser() {
        userInfoMapper.addUser(new UserInfo(1, "Jerry"));
        userInfoMapper.addUser(new UserInfo(2, "はなさき"));
        userInfoMapper.addUser(new UserInfo(3, "ちゆき"));
        userInfoMapper.addUser(new UserInfo(4, "ももざわ"));
        userInfoMapper.addUser(new UserInfo(5, "うゆき"));
        userInfoMapper.addUser(new UserInfo(6, "ことなみ"));
        userInfoMapper.addUser(new UserInfo(7, "すずらん"));
        userInfoMapper.addUser(new UserInfo(8, "あきやま"));
        userInfoMapper.addUser(new UserInfo(9, "ゆきな"));
        userInfoMapper.addUser(new UserInfo(10, "ほしの"));
        userInfoMapper.addUser(new UserInfo(11, "すずみ"));
        userInfoMapper.addUser(new UserInfo(12, "みどり"));
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

    /**
     * 查询全部用户
     */
    @Test
    public void getUsers() {
        List<UserInfo> list = userInfoMapper.getUsers();
        list.forEach(item ->
                System.out.println(item.getId() + ":" + item.getName())
        );
    }

    /**
     * 分页查询用户
     */
    @Test
    public void getUsersByPage() {
        int pageIndex = 1;
        int pageSize = 5;
        int page = (pageIndex - 1) * pageSize;
        List<UserInfo> list = userInfoMapper.getUsersByPage(page, pageSize);
        list.forEach(item ->
                System.out.println(item.getId() + ":" + item.getName())
        );
    }

    /**
     * 删除
     */
    @Test
    public void deleteUser() {
        userInfoMapper.deleteUser(1);

        List<UserInfo> userInfos = userInfoMapper.getUsers();
        for (UserInfo userInfo : userInfos) {
            System.out.println(String.format("ID=%s;NAME=%s", userInfo.getId(), userInfo.getName()));
        }
    }
}
