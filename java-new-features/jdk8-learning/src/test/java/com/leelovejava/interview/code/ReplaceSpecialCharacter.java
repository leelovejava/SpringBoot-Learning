package com.leelovejava.interview.code;

/**
 * 问题: 57. 有一串无序的任意序列字符串，包含了一些特殊字符，现在需要去除特殊字符，只留下数字字母下划线。
 *
 * 例如 aabb23xx&caaccy#yxx@xyyd5_d4dd”处理后需要留下结果为：“aabb23xxcaaccyyxxxyyd5_d4dd”。
 *
 * 思路：如果使用循环判断字符类型也可以处理，但是太麻烦，所以使用正则表达式会很方便。\W 在正则表达式中代表的是非数字字母下划线的字符。这样可以使用此表达式替换成空字符。
 * @author leelovejava
 * @date 2020/4/27 21:18
 **/
public class ReplaceSpecialCharacter {
    public static void main(String[] args) {
        String str = "aabb23xx&caaccy#yxx@xyyd5_d4dd";
        str = str.replaceAll("[\\W]", "");
        System.out.println(str);
    }
}
