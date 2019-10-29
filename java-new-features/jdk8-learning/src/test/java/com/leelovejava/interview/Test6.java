package com.leelovejava.interview;

import org.junit.Test;

import java.util.Scanner;

public class Test6 {
    /**
     * 题目：输入一个已经按升序排序过的数组和一个数字，在数组中查找两个数，使得它们的和正好是输入的那个数字。要求时间复杂度是O(n)。如果有多对数字的和等于输入的数字，输出任意一对即可。
     * 例如输入数组1、2、4、7、11、15和数字15。由于4+11=15，因此输出4和11。
     * 分析：如果我们不考虑时间复杂度，最简单想法的莫过去先在数组中固定一个数字，再依次判断数组中剩下的n-1个数字
     * 与它的和是不是等于输入的数字。可惜这种思路需要的时间复杂度是O(n2)。
     */
    @Test
    public void test3() {
        Scanner s = new Scanner(System.in);
        System.out.println("请输入数组的长度");
        int length = s.nextInt();
        int[] arr = new int[length];
        System.out.println("请依次输入各个自然数：");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = s.nextInt();
        }
        System.out.println("请输入你要查找的两数的和：");
        int num = s.nextInt();

        int x = 0, y = arr.length - 1;
        while (x < y) {
            if (num > arr[x] + arr[y]) {
                x++;
            } else if (num < arr[x] + arr[y]) {
                y--;
            } else {
                System.out.println("这两个自然数为：");
                System.out.println(arr[x] + "," + arr[y]);
                break;
            }
        }


    }
}
