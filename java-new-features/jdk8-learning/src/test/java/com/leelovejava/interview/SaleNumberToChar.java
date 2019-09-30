package com.leelovejava.interview;

import java.util.Scanner;

/**
 * @author ACER
 * <p>
 * 本程序用于将财务的数字类型表示转换为对应的大写汉字表示，可处理1亿亿(小数点之前16位)以内的数值。
 */
public class SaleNumberToChar {
    // 转换字符的常量数组
    char[] zh_cnChar = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    char[] weightChar = {'仟', '佰', '拾', '角', '分'};
    char[] priceChar = {'元', '萬', '亿', '萬'};

    public static void main(String[] args) {
        SaleNumberToChar toChar = new SaleNumberToChar();

        String reqTransferNumber = toChar.getNumber();

        String resultStr = toChar.getTransfer(reqTransferNumber);
        System.out.println("转换结果：\n" + resultStr);

    }

    // 获取用户输入
    public String getNumber() {
        String reqStr = "";
        Scanner s = new Scanner(System.in);
        boolean flag = false;
        System.out.println("请输入需要转换的数值串(1亿亿以内/整数部分不超过16位)：");
        while (!flag) {
            try {
                reqStr = s.next();
                reqStr = trimZero(reqStr);
                Double.parseDouble(reqStr);
                flag = true;
                s.close();
            } catch (Exception e) {
                System.out.println("请输入有效数值！");
            }
        }

        return reqStr;
    }

    public String getTransfer(String f) {
        String str = f;
        String result = "", emp;
        // split()函数在处理"."号时需要转义，因为这是个ASCII字符。
        String[] splitArray = str.split("\\.");

        int len = splitArray[0].length();
        if (len > 16) {
            System.out.println("超出处理范围！");
            System.exit(0);
        }
        // 处理整数部分的值，由低位至高位，每4位一组，最后不足4位的取全部
        for (int i = 0; i <= len / 4; i++) {
            if (len - 4 * (i + 1) < 0) {
                emp = splitArray[0].substring(0, len - 4 * i);
            } else {
                emp = splitArray[0].substring(len - 4 * (i + 1), len - 4 * i);
            }
            emp = deciBefor(emp);
            // 当取到的4位都是0时，判断是否要添加这4位对应的权值字符
            if ((i % 2 != 0 || i == 4) && "".equals(emp)) {
                continue;
            }
            // 每4位添加一个对应的权值字符
            emp += priceChar[i] + "  ";
            result = emp + result;
        }

        // 若有小数点，则处理小数部分，小数部分取两位
        if (splitArray.length > 1) {
            int slen = splitArray[1].length();
            slen = slen > 2 ? 2 : slen;
            emp = splitArray[1].substring(0, slen);
            emp = deciAfter(emp);
            result += emp;
        } else {
            result += "整";
        }
        // 若最终有前导"零"，则去掉此"零"
        if (result.startsWith("零")) {
            result = result.substring(1, result.length());
        }
        return result;
    }

    // 对小数部分的转换
    public String deciAfter(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int num = s.charAt(i) - 48;
            str += "" + zh_cnChar[num] + weightChar[3 + i];
        }
        return str;
    }

    // 对整数部分的转换，每4位一组
    public String deciBefor(String s) {
        s = trimZero(s);
        if (s == "") {
            return "";
        }
        String str = "";
        // j为weightChar[]数组准备，匹配位的权值
        for (int i = s.length() - 1, j = 3; i >= 0; i--, j--) {
            int num = s.charAt(i) - 48;
            char cNum = zh_cnChar[num];
            if (num == 0) {
                str = "" + cNum + str;
            } else {
                str = "" + cNum + (j == 3 ? "" : weightChar[j]) + str;
            }
        }
        // 对零位数的处理，类似3000,3300,3003,3330
        str = str.replace("零零零", "");
        str = str.replace("零零", "零");
        if (str.endsWith("零")) {
            str = str.substring(0, str.length() - 1);
        }
        // 若不足4位字符，则添加"零"前导，类似0003，0033，0333
        if (s.length() < 4) {
            str = "零" + str;
        }
        return str;
    }

    // 去掉前导0
    public String trimZero(String s) {
        // 判断是否为""——刚好是4倍数的数字最后会取到一个空字符串，或者取到了中间的4个0——0000
        if ("".equals(s)) {
            return "";
        } else if (Double.parseDouble(s) == 0) {
            return "";
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0') {
                continue;
            }
            return s.substring(i, s.length());
        }
        return null;
    }
}
