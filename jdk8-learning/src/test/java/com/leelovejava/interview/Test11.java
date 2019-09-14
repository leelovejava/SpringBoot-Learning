package com.leelovejava.interview;

/**
 * 金额转换，阿拉伯数字的金额转换成中国传统的形式
 * 如：105600123 => 壹亿零仟伍佰陆拾零万零仟壹佰贰拾叁圆整
 * 扩展：问题1：反转。   问题2：带上小数，求角、分
 */
public class Test11 {
    public static void main(String[] args) {
        String money = convert(105600123);
        System.out.println(money);

        int i = convert1(money);
        System.out.println(i);
    }

    private static final char[] data = new char[]{'零', '壹', '贰', '叁', '肆',
            '伍', '陆', '柒', '捌', '玖'};

    private static final char[] units = new char[]{'圆', '拾', '佰', '仟', '万',
            '拾', '佰', '仟', '亿'};

    public static int convert1(String money) {
        char[] m = money.toCharArray();
        int length = m.length;
        StringBuffer sb = new StringBuffer();
        for (int i = length - 3; i >= 0; i -= 2) {
            switch (m[i]) {
                case '零':
                    sb.insert(0, 0);
                    break;
                case '壹':
                    sb.insert(0, 1);
                    break;
                case '贰':
                    sb.insert(0, 2);
                    break;
                case '叁':
                    sb.insert(0, 3);
                    break;
                case '肆':
                    sb.insert(0, 4);
                    break;
                case '伍':
                    sb.insert(0, 5);
                    break;
                case '陆':
                    sb.insert(0, 6);
                    break;
                case '柒':
                    sb.insert(0, 7);
                    break;
                case '捌':
                    sb.insert(0, 8);
                    break;
                case '玖':
                    sb.insert(0, 9);
                    break;

            }
        }

        return Integer.parseInt(sb.toString());
    }

    public static String convert(int money) {
        StringBuffer sb = new StringBuffer("整");
        int unit = 0;
        while (money != 0) {
            sb.insert(0, units[unit++]);
            int number = money % 10;
            sb.insert(0, data[number]);
            money /= 10;
        }
        return sb.toString();
    }
}
