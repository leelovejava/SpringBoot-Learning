package com.atguigu.java1.record;

import java.util.HashSet;

/**
 * JEP 384：Records Class（预览）
 *
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 15:59
 */
public class RecordTest {

    /**
     * JEP 384：Records Class（预览）
     * Records Class 也是第二次出现的预览功能，它在 JDK 14 中也出现过一次了，使用 Record 可以更方便的创建一个常量类，使用的前后代码对比如下。
     * <p>
     * 当你用Record 声明一个类时，该类将自动拥有以下功能：
     * 获取成员变量的简单方法，以上面代码为例 name() 和 partner() 。注意区别于我们平常getter的写法。
     * 一个 equals 方法的实现，执行比较时会比较该类的所有成员属性
     * 重写 equals 当然要重写 hashCode
     * 一个可以打印该类所有成员属性的 toString 方法。
     * 请注意只会有一个构造方法。
     *
     * @param args
     */
    public static void main(String[] args) {
        Customer cust1 = new Customer("罗密欧", new Customer("朱丽叶", null));

        System.out.println(cust1.toString());
        // 类似于原有的针对于实例变量的get()
        System.out.println(cust1.name());
        // 类似于原有的针对于实例变量的get()
        System.out.println(cust1.partner());

        HashSet<Customer> set = new HashSet<>();
        set.add(cust1);

        Customer cust2 = new Customer("罗密欧", new Customer("朱丽叶", null));
        set.add(cust2);

        set.forEach(System.out::println);
    }
}
