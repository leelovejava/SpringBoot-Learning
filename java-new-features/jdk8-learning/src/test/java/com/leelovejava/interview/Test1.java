package com.leelovejava.interview;

/**
 * 题目1：一个数组，让数组的每个元素去除第一个元素，得到的商作为被除数所在位置的新值。
 */
public class Test1 {
    public static void main(String[] args) {
        int[] arr = new int[]{12, 43, 65, 3, -8, 64, 2};

//		for(int i = 0;i < arr.length;i++){
//			arr[i] = arr[i] / arr[0];
//		}
        for (int i = arr.length - 1; i >= 0; i--) {
            arr[i] = arr[i] / arr[0];
        }
        //遍历arr
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
}
