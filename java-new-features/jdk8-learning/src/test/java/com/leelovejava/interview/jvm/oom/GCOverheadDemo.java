package com.leelovejava.interview.jvm.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leelovejava
 * @date 2019/10/31 21:53
 * JVM参数配置演示
 * MaxDirectMemorySize 最大的直接内存
 * -Xms10m -Xms10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
 * <p>
 * GC回收时间过长时会抛出OutOfMemoryError.过长的定义是,超过98%的时间用来做GC,并且回收了不到2%的堆内存
 * 那就是GC清理的这么点内存很快会再次填满,迫使GC再次执行,这样就形成恶心循环,
 * CPU使用率一直是100%,而GC却没有任何成果
 **/
public class GCOverheadDemo {

    public static void main(String[] args) {
        int i = 0;
        List<String> list = new ArrayList<>();
        try {
            list.add(String.valueOf(++i).intern());
        } catch (Throwable e) {
            System.out.println("*************i:" + i);
            e.printStackTrace();
            throw e;
        }
    }
}
