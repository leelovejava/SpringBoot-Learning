package com.leelovejava.interview;

import org.junit.Test;

import java.io.UnsupportedEncodingException;


/**
 * 编写一个截取字符串的函数，输入为一个字符串和字节数，输出为按字节截取的字符串。
 * 但是要保证汉字不被截半个，如"我ABC"4，应该截为"我AB"，输入"我ABC汉DEF"，6，
 * 应该输出为"我ABC"而不是"我ABC+汉的半个"?
 **/
public class Test9 {

    public static void main(String[] args) {
        String str = "我a爱北京abc我爱尚硅谷def";
        int num = 0;
        try {
            num = trimGBK(str.getBytes("GBK"), 6);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(str.substring(0, num));

    }

    public static int trimGBK(byte[] buf, int n) {
        int num = 0;
        boolean bChineseFirstHalf = false;
        for (int i = 0; i < n; i++) {
            if (buf[i] < 0 && !bChineseFirstHalf) {
                bChineseFirstHalf = true;
            } else {
                num++;
                bChineseFirstHalf = false;
            }
        }
        return num;
    }

    @Test
    public void test1() {
        for (int i = 0; i <= 255; i++) {
            char c = '中';
            System.out.println((int) c);
        }
    }
}
