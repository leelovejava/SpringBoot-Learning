package com.leelovejava.interview.code;

/**
 * 问题: 59.一个有 n 级的台阶，一次可以走 1 级、2 级或 3 级，问走完 n 级台阶有多少种走法
 * 思路：本题使用递归可以很方便求出结果。台阶级数任意，但是走法只有三种，所以每次递归计算三种执行次数就可得出结果。
 *
 * @author leelovejava
 * @date 2020/4/27 21:11
 **/
public class GoSteps {
    /**
     * 可以通过递归求解
     *
     * @param n
     * @return
     */
    public static int countWays(int n) {
        if (n < 0) {
            return 0;
        } else if (n == 0) {
            return 1;
        } else {
            return countWays(n - 1) + countWays(n - 2) + countWays(n - 3);
        }
    }

    public static void main(String[] args) {
        System.out.println(countWays(5)); // 13
    }
}
