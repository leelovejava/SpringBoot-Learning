package com.leelovejava.interview.code;

/**
 * 问题: 58.写一个算法判断一个英文单词的所有字母是否全都不同（不区分大小写）
 * 思路：循环逐一取出每个字母，并放入数组中，如果后面取出的字符在数组中存在则直接为假。
 * 如果整个循环结束后，还是没有重复的在数组中。则认为是没有重复的。
 * @author leelovejava
 * @date 2020/4/27 21:18
 */
public class AllNotTheSame {
    public static boolean judge(String str) {
        String temp = str.toLowerCase();
        int[] letterCounter = new int[26];
        for (int i = 0; i < temp.length(); i++) {
            int index = temp.charAt(i) - 'a';
            letterCounter[index]++;
            if (letterCounter[index] > 1) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(judge("hello"));
        System.out.print(judge("smile"));
    }
}
