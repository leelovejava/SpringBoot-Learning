package com.leelovejava.interview;

/**
 * 已知有一个数列：f(0) = 1,f(1) = 4,
 * f(n+2)=2*f(n+1) + f(n),==>f(n) = 2*f(n-1) + f(n-2)
 * 其中n是大于0的整数，求f(10)的值。
 **/
public class Test13 {
    public static void main(String[] args) {
        Test13 t = new Test13();
        int i = t.func(10);
        System.out.println(i);
    }

    public int func(int n) {
        if (n == 0)
            return 1;
        else if (n == 1)
            return 4;
        else {
            return 2 * func(n - 1) + func(n - 2);
            //return func(n+2)-2*func(n+1);
        }
    }
}
