package com.leelovejava.gc;

/**
 * 类描述：jvm判断对象生死
 * GC回收:根搜索算法
 *
 * @version 1.0
 * @since jdk1.7
 */
public class FinallizeGc {

    private static FinallizeGc fg = null;

    public void isActive() {
        System.out.println("我还活着");
    }

    /**
     * 一个对象的finallize()方法只会被执行一次，意味着对象只能使用finallize方法自救一次
     */
    @Override
    protected void finalize() throws Throwable {
        System.out.println("第一次标记：执行finallize方法。进行自救，使fg指向当前对象。");
        fg = this;
    }

    /**
     * 根搜索算法判定对象是否可回收,至少要经历两次标记过程
     *
     * @param args
     * @throws InterruptedException
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws InterruptedException {
        fg = new FinallizeGc();

        // 测试第一次垃圾回收
        fg = null;
        //手动垃圾回收
        System.gc();
        //判断对象受否存货  
        System.out.println("第一次垃圾回收自救结果：");
        //因为finallizer线程优先级较低，当前线程暂停1秒，等待finallizer执行  
        Thread.sleep(1000);
        if (fg != null) {
            fg.isActive();
        } else {
            System.out.println("I am die!");
        }

        // 测试第二次垃圾回收
        fg = null;
        //手动垃圾回收  
        System.gc();
        // 判断对象受否存货
        System.out.println("第二次垃圾回收自救结果：");
        Thread.sleep(1000);
        if (fg != null) {
            fg.isActive();
        } else {
            System.out.println("I am die!");
        }
    }
} 