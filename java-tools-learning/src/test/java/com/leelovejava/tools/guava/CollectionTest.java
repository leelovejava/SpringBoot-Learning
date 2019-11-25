package com.leelovejava.tools.guava;

import com.google.common.collect.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CollectionTest
 *
 * @author y.x
 * @date 2019/11/25
 */
public class CollectionTest {
    /**
     * 集合的创建
     *
     * 不可变集合是线程安全的，并且中途不可改变，因为add等方法是被声明为过期，并且会抛出异常
     * public final void add(int index, E element) {
     * 	throw new UnsupportedOperationException();
     * }
     */
    public void test01() {
        // 普通集合的创建
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();

        // 不可变集合的创建
        ImmutableList<String> list2 = ImmutableList.of("a", "b", "c");
        ImmutableSet<String> set2 = ImmutableSet.of("a", "b");
    }

    public void test02() {
        // use java
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

        // use guava
        Multimap<String, Integer> map2 = ArrayListMultimap.create();
        map2.put("key1", 1);
        map2.put("key1", 2);
        // [1, 2]
        System.out.println(map2.get("key1"));
    }

    /**
     * 2个键映射一个值
     */
    public void test03() {
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("a", "a", 1);
        table.put("a", "b", 2);
        // 2
        System.out.println(table.get("a", "b"));
    }
}
