package com.leelovejava.drools.rules.mvel;

import com.google.common.collect.Maps;
import org.mvel2.MVEL;

import java.util.Map;

/**
 * MVEL表达式解析器
 *
 * @author leelovejava
 * @date 2019/11/24
 */
public class MvelUtils {

    public static void main(String[] args) {
        String expression = "a == null && b == nil ";
        Map<String, Object> map = Maps.newHashMap();
        map.put("a", null);
        map.put("b", null);

        Object object = MVEL.eval(expression, map);
        System.out.println(object);
    }

}