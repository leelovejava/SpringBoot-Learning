package com.leelovejava.interview;
/*
 * 古典问题：有一对兔子，从出生后第3个月起每个月都生一对兔子，小兔子长到第三个月后每个月又生一对兔子，
 * 假如兔子都不死，问每个月的兔子总数为多少？   
//这是一个菲波拉契数列问题

 */
public class Test15 {
	public static void main(String[] args) {
		System.out.println("第1个月：" + 1);
		System.out.println("第2个月：" + 1);
		int M = 24;
		int f1 = 1,f2 = 1;
		int f;
		for(int i = 3;i <= M;i++){
			f = f2;
			f2 = f1 + f2;
			f1 = f;
			System.out.println("第" + i + "个月：" + f2);
		}
	}
}
