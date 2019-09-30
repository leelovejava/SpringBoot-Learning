package com.atguigu.java;

import org.junit.Test;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author shkstart
 * @create 2019 下午 8:03
 */
public class NumberFormatTest {
    /**
     * 支持压缩数字格式化
     * NumberFormat 添加了对以紧凑形式格式化数字的支持。紧凑数字格式是指以简短或人类可读形式表示的数字。例
     * 如，在en_US语言环境中，1000可以格式化为“1K”，1000000可以格式化为“1M”，具体取决于指定的样式
     * NumberFormat.Style
     */
    @Test
    public void testCompactNumberFormat() {
        var cnf = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        System.out.println(cnf.format(1_0000));
        System.out.println(cnf.format(1_9200));
        System.out.println(cnf.format(1_000_000));
        System.out.println(cnf.format(1L << 30));
        System.out.println(cnf.format(1L << 40));
        System.out.println(cnf.format(1L << 50));
    }
}
