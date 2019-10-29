package com.leelovejava.interview.jvm.ref;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 引用使用场景
 * @author leelovejava
 * @date 2019/10/29 22:33
 **/
public class ReferenceScenes {

    /**
     * 假如一个应用需要读取大量的图片:
     * 如果每次读取图片都从硬盘读取则会严重影响性能
     * 如果一次全部加载到内存又可能造成内存溢出
     *
     * 此时使用软引用可以解决这个问题.
     * 设计思路是:用一个HashMap来保存图片的路径和相应的图片对象关联的软引用之间的映射关系,在内存不足时,JVM会自动回收这些缓存图片对象所占用的空间,从而有效的避免了OOM问题
     * @param args
     */
    public static void main(String[] args) {
        Map<String, SoftReference<BitMap>> imageCache = new HashMap<String,SoftReference<BitMap>>();
    }
}
