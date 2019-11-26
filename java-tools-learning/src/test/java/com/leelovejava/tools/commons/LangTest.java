package com.leelovejava.tools.commons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * LangTest
 *
 * @author y.x
 * @date 2019/11/25
 */
public class LangTest {

    public void test01() {
        // isEmpty的实现 cs == null || cs.length() == 0; return true
        assertEquals(true, StringUtils.isEmpty(""));

        assertEquals(true, StringUtils.isBlank(null));
        assertEquals(true, StringUtils.isBlank(""));
        // 空格
        assertEquals(true, StringUtils.isBlank(" "));
        // 回车
        assertEquals(true, StringUtils.isBlank("    "));
    }

    /**
     * Pair和Triple 当想返回2个或3个值，但这几个值没有相关性，没有必要单独封装一个对象，就可以用到如下数据结构，返回Pair或Triple对象
     */
    public void test02() {
        Pair<Integer, Integer> pair = new ImmutablePair<>(1, 2);
        // 1 2
        System.out.println(pair.getLeft() + " " + pair.getRight());

        Triple<Integer, Integer, Integer> triple = new ImmutableTriple<>(1, 2, 3);
        // 1 2 3
        System.out.println(triple.getLeft() + " " + triple.getMiddle() + " " + triple.getRight());
    }

    private void assertEquals(boolean b, boolean empty) {

    }
}
