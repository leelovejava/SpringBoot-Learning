package com.leelovejava.interview;

import java.util.Scanner;

/**
 * 大整数相乘
 *
 * @author y.x
 * @date 2019/12/21
 */
public class MultiBigIntTest {


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String x, y, ans;
        while (in.hasNext()) {
            x = in.next();
            y = in.next();
            ans = mul(x, y);
            System.out.println(ans);
        }

    }

    public static String mul(String x, String y) {
        if (x.charAt(0) == '-' && y.charAt(0) == '-') return _mul(x.substring(1), y.substring(1));
        if (x.charAt(0) == '-' && y.charAt(0) != '-') return _mul(x.substring(1), y);
        if (x.charAt(0) != '-' && y.charAt(0) != '-') return _mul(x, y);
        if (x.charAt(0) != '-' && y.charAt(0) == '-') return _mul(x, y.substring(1));
        return "0";
    }

    public static String _mul(String x, String y) {
        String ans = "0";
        int cnt = 0;
        y = new StringBuffer(y).reverse().toString();
        while (true) {
            if (x.length() == 0) break;
            char ch = x.charAt(x.length() - 1);
            int carry = 0;
            String tmp = "";
            for (int i = 0; i < y.length(); i++) {
                carry = carry + (y.charAt(i) - '0') * (ch - '0');
                tmp += "" + carry % 10;
                carry /= 10;
            }
            tmp += carry;
            tmp = new StringBuffer(tmp).reverse().toString();

            for (int i = 0; i < cnt; i++) {
                tmp += "0";
            }
            cnt++;

            ans = add(ans, tmp);
            x = x.substring(0, x.length() - 1);
        }
        if (ans.equals("")) ans = "0";
        return ans;
    }

    public static String add(String x, String y) {
        int k = 0;
        while (k < x.length() && x.charAt(k) == '0') k++;
        x = x.substring(k);
        k = 0;
        while (k < y.length() && y.charAt(k) == '0') k++;
        y = y.substring(k);


        x = new StringBuffer(x).reverse().toString();
        y = new StringBuffer(y).reverse().toString();

        String ans = "";
        int carry = 0, i, j;
        for (i = 0, j = 0; i < x.length() && j < y.length(); i++, j++) {
            carry += (x.charAt(i) - '0') + (y.charAt(j) - '0');
            ans += carry % 10;
            carry /= 10;
        }

        for (; i < x.length(); i++) {
            carry += x.charAt(i) - '0';
            ans += carry % 10;
            carry /= 10;
        }

        for (; j < y.length(); j++) {
            carry += y.charAt(j) - '0';
            ans += carry % 10;
            carry /= 10;
        }

        ans += carry;
        ans = new StringBuffer(ans).reverse().toString();
        k = 0;
        while (k < ans.length() && ans.charAt(k) == '0') k++;
        ans = ans.substring(k);
        if (ans.equals("")) ans = "0";
        return ans;
    }
}
