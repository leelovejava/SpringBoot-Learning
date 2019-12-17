package com.atguigu.java;

import java.util.ArrayList;
import java.util.List;


/**
 * zgc 全新的垃圾回回收器,在数TB的堆上具有非常低的暂停时间
 * GC暂停时间不会超过10ms
 * 既能处理几百兆的小堆, 也能处理几个T的大堆(OMG)
 * 和G1相比, 应用吞吐能力不会下降超过15%
 * 为未来的GC功能和利用colord指针以及Load barriers优化奠定基础
 * 初始只支持64位系统
 *
 * @author leelovejava
 */
public class ZgcTest {

    /**
     * 执行: -XX:+UnlockExperimentalVMOptions -XX:+UseZGC
     *  ZGC是一个并发, 基于region, 压缩型的垃圾收集器, 只有root扫描阶段会STW, 因此GC停顿时间不会随着堆的增长和存活对象的增长而变长
     * @param args
     */
    public static void main(String[] args) {
        List<Garbage> list = new ArrayList<>();
        boolean flag = true;
        int count = 0;
        while (flag) {
            list.add(new Garbage());
            if (count++ % 500 == 0) {
                list.clear();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
