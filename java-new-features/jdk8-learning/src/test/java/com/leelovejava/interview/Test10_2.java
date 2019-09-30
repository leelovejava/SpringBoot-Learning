package com.leelovejava.interview;

import org.junit.Test;

public class Test10_2 {
    @Test
    public void test1() {
        String str = "aaadddeeffbbbb";
        String newStr = "";
        int num = 1;
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) == str.charAt(i + 1)) {
                num++;
                if (i == str.length() - 2) {

                }
            } else {
                newStr += (str.charAt(i) + "" + num);
                num = 1;
            }
        }
        int n = 1;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                n++;

            } else {
                newStr += (str.charAt(i) + "" + n);
                break;
            }

        }
        System.out.println(newStr);
    }


}
