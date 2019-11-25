package com.leelovejava.tools.commons;

import com.leelovejava.tools.commons.model.User;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * BeanUtils
 * 提供了一系列对java bean的操作，读取和设置属性值等
 *
 * @author y.x
 * @date 2019/11/25
 */
public class BeanUtilsTest {
    /**
     * 读取和设置属性值
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public void test01() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        User user = new User();
        BeanUtils.setProperty(user, "username", "li");
        BeanUtils.getProperty(user, "username");
    }

    /**
     * map和bean的互相转换
     *
     * 将对象放在缓存中通常用redis中的hash，如下
     * # 设置用户信息
     * hset student name test
     * hset student age 10
     * 这种场景下map和bean的互相转换的工具类就特别有用
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public void test02() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        User user = new User();
        // bean->map
        Map<String, String> map = BeanUtils.describe(user);
        // map->bean
        BeanUtils.populate(user, map);
    }
}
