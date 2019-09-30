package com.leelovejava.interview;

import java.util.Scanner;

import org.junit.Test;

public class Test10 {
	/*
	 * 在一个字符串中找到第一个只出现一次的字符。如输入abaccdeff，则输出b。 
	 */
	@Test
	public void test10(){
		String str = "abaccdeff";
		int[] arr = new int[256];
		for(int i = 0;i < str.length();i++){
			arr[str.charAt(i)]++;
		}
		
//		for(int i = 0;i < arr.length;i++){
//			System.out.print(arr[i] + " ");
//			if(i % 20 == 0){
//				System.out.println();
//			}
//		}
		System.out.println();
		for(int i = 0;i < str.length();i++){
			if(arr[str.charAt(i)] == 1){
				System.out.println(str.charAt(i));
				break;
			}
		}
	}
}
