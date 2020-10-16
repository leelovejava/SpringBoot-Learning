package com.atguigu.java1;

import org.junit.Test;

/**
 * JEP 375：Pattern Matching for instanceof (Second Preview) instanceof 自动匹配模式
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 22:42
 */
public class InstanceofTest {
    //新特性之前：
    @Test
    public void test1(){
        Object obj = new String("hello,before Java14");
        obj = null;
        if(obj instanceof String){
            String str = (String) obj;//必须显式的声明强制类型转换。
            System.out.println(str.contains("Java"));
        }else{
            System.out.println("非String类型");
        }
    }
    //使用新特性
    @Test
    public void test2(){
        Object obj = new String("hello,Java15");
        obj = null;
        if(obj instanceof String str){ //新特性：省去了强制类型转换的过程
            System.out.println(str.contains("Java"));
        }else{
            System.out.println("非String类型");
        }
    }
}
//再举例1
class Monitor{
    private String model;
    private double price;
    //使用新特性：
//    public boolean equals(Object o){
//        if(o instanceof Monitor other){
////            Monitor other = (Monitor)o;
//            if(model.equals(other.model) && price == other.price){
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean equals(Object o){
        return o instanceof Monitor other && model.equals(other.model) && price == other.price;
    }

}
// 再举例2
class InstanceOf{

    String str = "abc";

    public void test(Object obj){

        if(obj instanceof String str){//此时的str的作用域仅限于if结构内。
            System.out.println(str.toUpperCase());
        }else{
            System.out.println(str.toLowerCase());
        }
    }

}


