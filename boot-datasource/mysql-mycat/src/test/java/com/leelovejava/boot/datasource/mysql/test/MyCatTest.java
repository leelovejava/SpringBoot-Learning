package com.leelovejava.boot.datasource.mysql.test;

import com.leelovejava.boot.datasource.mycat.MyCatApplication;
import com.leelovejava.boot.datasource.mycat.dao.UserDao;
import com.leelovejava.boot.datasource.mycat.model.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * mycat测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MyCatApplication.class})
public class MyCatTest {
    @Resource
    private UserDao userDao;

    @Test
    public void testAdd() {
        for (long i = 0; i < 50; i++) {
            UserModel user = new UserModel();
            user.setId(i);
            user.setName("ls" + i);
            userDao.save(user);
        }
    }
}
