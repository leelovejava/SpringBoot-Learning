package com.leelovejava.jvisualvm;

/**
 * 如何判断java对象已经死亡？
 * 引用计数算法:给对象添加一个引用计数器,每当有一个地方引用到他，就加1;引用失效就减1
 * @author tianhao
 */
public class ReferenceCountingGC {

    public Object instace = null;
    /**
     * 一个200M的对象
     */
    private byte[] bigSize = new byte[200 * 1024 * 1024];

    public static void main(String[] args) {
//      引用加1
        ReferenceCountingGC a = new ReferenceCountingGC();
        ReferenceCountingGC b = new ReferenceCountingGC();
//      引用再加1
        a.instace = b;
        b.instace = a;
//      如果是引用计数，那么a,b的引用为2
//      在这里a,b减1
        a = null;
        b = null;
//      那么在这里ab引用都不是0，应该不可以回收
        System.gc();
        sleep(30);
    }

    /**
     * debug，通过VisualVM发现，内存仍然被回收了
     * 所以JVM不是通过引用计数的方式来确定是否回收对象的。
     * 因为,引用计数从上面的例子看出来会有一个问题就是，虽然还有引用，但这两个对象都是不可达对象（无法被访问的）
     * @param second
     */
    public static void sleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}