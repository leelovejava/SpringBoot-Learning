package com.leelovejava.drools.rules.aviator;

import com.googlecode.aviator.AviatorEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leelovejava
 * @date 2019/11/24
 */
public class Test {
    /**
     * 执行方式
     * 执行表达式的方法有两个：execute()、exec();
     * execute()，需要传递Map格式参数
     * exec(),不需要传递Map
     *
     * @param args
     */
    public static void main(String[] args) {
        // exec执行方式，无需传递Map格式
        String age = "18";
        System.out.println(AviatorEvaluator.exec("'His age is '+ age +'!'", age));


        // execute执行方式，需传递Map格式
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("age", "18");
        System.out.println(AviatorEvaluator.execute("'His age is '+ age +'!'",
                map));

    }
}