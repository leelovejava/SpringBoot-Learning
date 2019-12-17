package com.atguigu.java;

import org.junit.Test;

import java.io.FileInputStream;

/**
 * 增加了一系列的字符串处理方法
 *
 * @author leelovejava
 */
public class StringTest {

    @Test
    public void testName3() throws Exception {
        FileInputStream fis = new FileInputStream("src/com/atguigu/java/StringTest.java");
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        String string = new String(buffer);
        string.lines().forEach(System.out::println);
    }

    @Test
    public void testName2() {
        String string = "Java";
        // 复制字符串 5次
        String string2 = string.repeat(5);
        System.out.println(string2);
    }

    @Test
    public void testName() {
        String string = " \t  \r\n ";
        // 判断字符串中的字符是否都是空白
        System.out.println(string.isBlank());

        System.out.println("**************************");

        string = " \t  \r\n abc \t　";
        // 去重字符串首尾的空白, 包括英文和其他所有语言中的空白字符
        String string2 = string.strip();
        System.out.println(string2);
        System.out.println(string2.length());
        // 去重字符串首尾的空白字符, 只能去除码值小于等于32的空白字符
        String string3 = string.trim();
        System.out.println(string3);
        System.out.println(string3.length());

        System.out.println("**************************");
        // 去重字符串首部的空白
        String string4 = string.stripLeading();
        System.out.println(string4);
        System.out.println(string4.length());
        // 去重字符串尾部的空白
        String string5 = string.stripTrailing();
        System.out.println(string5);
        System.out.println(string5.length());

        // 行数统计
        // 3
        System.out.println("A\nB\nC".lines().count());
    }
}	
