package com.leelovejava.interview;

import java.util.Scanner;

public class Test14 {


    /**
     * 方法二
     *
     * @param n
     */
    public void fengjie(int n) {
        for (int i = 2; i <= n / 2; i++) {
            if (n % i == 0) {
                System.out.print(i + "*");
                fengjie(n / i);
            }
        }
        System.out.print(n);
        System.exit(0);// /不能少这句，否则结果会出错
    }

    /**
     * 方法一
     *
     * @param n
     */
    public void fen(int n) {
        for (int i = 2; i <= n; i++) {
            if (n == i) {
                System.out.println(i);
                break;
            }

            if (n % i == 0) {
                System.out.print(i + "*");
                n /= i;
                i--;
            }
        }
    }

    public static void main(String[] args) {
        Test14 c = new Test14();
        Scanner s = new Scanner(System.in);
        int num = s.nextInt();

        System.out.print(num + "分解质因数：" + num + "=");
        c.fen(num);
    }

}
