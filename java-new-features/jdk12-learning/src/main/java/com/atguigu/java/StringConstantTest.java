package com.atguigu.java;

import java.util.Optional;

/**
 * JVM 常量 API
 * @author shkstart
 * @create 2019 下午 5:18
 */
public class StringConstantTest {

    private static void testDescribeConstable() {
        System.out.println("======test java 12 describeConstable======");
        String name = "尚硅谷Java高级工程师";
        Optional<String> optional = name.describeConstable();
        System.out.println(optional.get());
    }

    public static void main(String[] args) {
        testDescribeConstable();
    }
}
