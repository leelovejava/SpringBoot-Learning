package com.leelovejava.interview;

/**
 * 输入两个正整数m和n，求其最大公约数和最小公倍数。
 */
public class Test2 {
    public static void main(String[] args) {
        int m = 12;
        int n = 28;
        int max = (m > n) ? m : n;
        int min = (m < n) ? m : n;
        // 最大公约数
        for (int i = min; i >= 1; i--) {
            if (m % i == 0 && n % i == 0) {
                System.out.println(i);
                break;
            }
        }
        // 最小公倍数
        for (int i = max; i <= m * n; i++) {
            if (i % m == 0 && i % n == 0) {
                System.out.println(i);
                break;
            }
        }
    }
}
