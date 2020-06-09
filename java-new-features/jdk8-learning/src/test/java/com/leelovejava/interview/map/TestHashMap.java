package com.leelovejava.interview.map;

import java.util.HashMap;

/**
 * 测试HashMap
 *
 * @author leelovejava
 * @date 2020/4/27 23:03
 **/
public class TestHashMap {
    public static void main(String[] args) {
        TestHashMap testHashMap = new TestHashMap();
        //testHashMap.testPutAddEntry();
        System.out.println(testHashMap.hash("1"));

    }

    private void testPutAddEntry() {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("1", "a");
    }


    /**
     * JDK 1.8中对hash算法和寻址算法是如何优化的？
     * 1) hash&(n-1)和n取模,效果一样(数组的长度是2的n次方),但与运算效果好
     * 2) 低16位融合了高16位和低16位的特征,避免了hash冲突
     * @param key
     * @return
     */
    private final int hash(Object key) {
        int h;
        // jdk1.7
        // h = key.hashCode() 为第一步 取hashCode值
        // h ^ (h >>> 16)  为第二步 高位参与运算
        int hashCode = key.hashCode();
        System.out.println("hashCode: " + hashCode);
        //System.out.println("toBinaryString: " + toBinaryString(hashCode));
        //System.out.println(">>>: " + (hashCode >>> 16));
        //System.out.println("toBinaryString>>>: " + toBinaryString((hashCode >>> 16)));
        System.out.println(intToBinary(49, 32));// 00000000000000000000000000110001
        System.out.println(intToBinary((49 >>> 16), 32));// 00000000000000000000000000000000
        // 异或运算: 异1,同0
        System.out.println(intToBinary(49 ^ (49 >>> 16), 32));// 00000000000000000000000000110001
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 输出一个int的二进制数
     *
     * @param num
     */
    private static String toBinaryString(int num) {
        return Integer.toBinaryString(num);
    }

    /**
     * int转二进制,高位补0
     *
     * @param num    需转换值
     * @param bitNum 位数
     * @return
     */
    public static String intToBinary(int num, int bitNum) {
        String binaryStr = Integer.toBinaryString(num);
        while (binaryStr.length() < bitNum) {
            binaryStr = "0" + binaryStr;
        }
        return binaryStr;
    }
}
