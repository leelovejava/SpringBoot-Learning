package com.leelovejava.tools.guava;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.stream.IntStream;

/**
 * Guava布隆过滤器(boomFilter)
 *
 * @author y.x
 * @date 2019/11/25
 * @see 'https://blog.csdn.net/Revivedsun/article/details/94992323'
 */
public class BloomFilterTest {


    public void test01() {
        // 01 创建布隆过滤器时要传入期望处理的元素数量，及最期望的误报的概率。如下分别是500和0.01
        BloomFilter<Integer> filter = BloomFilter.create(
                Funnels.integerFunnel(),
                500,
                0.01);
        // 02 向过滤器中插入元素
        filter.put(1);
        filter.put(2);
        filter.put(3);

        // 03 我们添加了3个元素，并且定义了最大元素数量为500，因此我们的过滤器将会产生非常准确的结果。我们使用mightContain()方法来测试
        filter.mightContain(1);
        filter.mightContain(2);
        filter.mightContain(3);

        // 因为布隆过滤器是一种概率型数据结构，因此返回true表示元素有极大的概率存在。当返回false那么表示元素一定不存在。
        // 当我们创建布隆过滤器时，尽可能提供准确的元素数量。否则将会产生较高的误报率
        // 下面的例子表示集合最多5个元素，这样在实际使用时就会产生很高的误报率
        BloomFilter<Integer> filter2 = BloomFilter.create(
                Funnels.integerFunnel(),
                5,
                0.01);

        IntStream.range(0, 100_000).forEach(filter2::put);
    }


}
