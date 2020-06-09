package com.leelovejava.juc;

/**
 * 一、volatile 关键字：当多个线程进行操作共享数据时,可以保证内存中的数据可见。
 * 					  相较于 synchronized 是一种较为轻量级的同步策略。
 *
 * 注意：
 * 1. volatile 不具备“互斥性”
 * 2. volatile 不能保证变量的“原子性”
 */
public class TestVolatile {
	/**
	 * 内存可见性（Memory Visibility）: 当某个线程正在使用对象状态
	 * 而另一个线程在同时修改该状态，需要确保当一个线程修改了对象
	 * 状态后，其他线程能够看到发生的状态变化
	 * @param args
	 */
	public static void main(String[] args) {
		VolatileThreadDemo td = new VolatileThreadDemo();
		new Thread(td).start();

		while(true){
			if(td.isFlag()){
				System.out.println("------------------");
				break;
			}
		}

	}

}

class VolatileThreadDemo implements Runnable {

	private volatile boolean flag = false;

	@Override
	public void run() {

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		flag = true;

		System.out.println("flag=" + isFlag());

	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
