package com.leelovejava.tools.guava;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * WatchTest
 *
 * @author y.x
 * @date 2019/11/25
 */
public class WatchTest {
    /**
     * 查看某段代码的运行时间
     * TimeUnit 可以指定时间精度
     */
    public void test01() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        // do something
        long second = stopwatch.elapsed(TimeUnit.SECONDS);
    }
}
