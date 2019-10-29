package com.leelovejava.interview.jvm.ref;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * 你知道软引用的话,能谈谈WeakHashMap吗?
 * @author leelovejava
 * @date 2019/10/29 22:43
 **/
public class WeakHashMapDemo {

    public static void main(String[] args) {
        myHashMap();
        System.out.println("******************");
        myWeakHashMap();
    }

    /**
     * gc后回收
     */
    private static void myWeakHashMap() {
        WeakHashMap<Integer,String> map = new WeakHashMap<>();
        Integer key  = new Integer(2);
        String value = "WeakHashMap";
        map.put(key,value);
        System.out.println(map);

        key = null;
        System.out.println(map);

        System.gc();
        System.out.println(map+"\t"+map.size());
    }

    private static void myHashMap() {
        HashMap<Integer,String> map = new HashMap<>();
        Integer key  = new Integer(1);
        String value = "HashMap";
        map.put(key,value);
        System.out.println(map);

        key = null;
        System.out.println(map);

        System.gc();
        System.out.println(map+"\t"+map.size());
    }
}
