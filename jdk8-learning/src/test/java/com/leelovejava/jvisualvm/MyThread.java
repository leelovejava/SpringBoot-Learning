package com.leelovejava.jvisualvm;

/**
 * 线程分析
 *
 * @author tianhao
 */
public class MyThread extends Thread {

    public static void main(String[] args) {

        MyThread mt1 = new MyThread("Thread a");
        MyThread mt2 = new MyThread("Thread b");

        mt1.setName("My-Thread-1 ");
        mt2.setName("My-Thread-2 ");

        mt1.start();
        mt2.start();
    }

    public MyThread(String name) {
    }

    @Override
    public void run() {

        while (true) {

        }
    }


}