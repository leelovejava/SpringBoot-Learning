package com.leelovejava.interview;

import org.junit.Test;

public class Test7 {
    @Test
    public void method1() {
        int[] arr = new int[]{3, 23, 45, 6, 3, -8, 0, -6, 43, 4};

        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    @Test
    public void method2() {
        int[] arr = new int[]{3, 23, 45, 6, 3, -8, 0, -6, 43, 4};

        for (int i = 0; i < arr.length - 1; i++) {
            int temp = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[temp] > arr[j]) {
                    temp = j;
                }
            }
            if (temp != i) {
                int t = arr[temp];
                arr[temp] = arr[i];
                arr[i] = t;
            }
        }

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
