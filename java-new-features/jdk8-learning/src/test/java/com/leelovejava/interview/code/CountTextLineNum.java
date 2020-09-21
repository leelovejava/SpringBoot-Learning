package com.leelovejava.interview.code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 问题: 60.编写一个程序，统计文本文件中内容的总行数
 * 思路：这是一个 IO 流问题，而且是一个字符流，读取行数，BufferedReader 可以一次读取一行，所以直接使用这个流循环读取到文件末尾就可计算出总行数。
 *
 * @author leelovejava
 * @date 2020/4/27 21:13
 **/
public class CountTextLineNum {
    public static void main(String[] args) {
        try {
            FileReader fw = new FileReader("ResizeImage.java");
            BufferedReader br = new BufferedReader(fw);
            int count = 0;
            //一次读取一行
            while (br.readLine() != null) {
                count++;
            }
            System.out.println("总行数：" + count);
            br.close();
            fw.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
