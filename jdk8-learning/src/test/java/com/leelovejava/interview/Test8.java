package com.leelovejava.interview;

/**
 * 将一个字符串进行反转。将字符串中指定部分进行反转。比如将“abcdefg”反转为”abfedcg”
 */
public class Test8 {

	public static void main(String[] args) {
        String str = new String("abcdefg");
        str = reverseString(str, 2, 5);
        System.out.println(str);
    }

    public static String reverseString(String str, int start, int end) {
        char[] c = str.toCharArray();

        return reverseArray(c, start, end);
    }

    public static String reverseArray(char[] c, int start, int end) {
        for (int x = start, y = end; x < y; x++, y--) {
            char temp = c[x];
            c[x] = c[y];
            c[y] = temp;
        }
        return new String(c);
    }
}
