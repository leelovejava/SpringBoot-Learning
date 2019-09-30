package com.atguigu.java;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author shkstart
 * @create 2019 下午 8:19
 */
public class StringTest {
    @Test
    public void testTransform() {
        // 它提供的函数作为输入提供给特定的String实例，并返回该函数返回的输出
        String info1 = "  hello".transform(info -> info + "world  ");
        System.out.println(info1);
    }


    @Test
    public void testTransform1() {
        // 传入一个函数式接口 Function，接受一个值，返回一个值，参考：Java 8 新特性之函数式接口
        //   hello --> helloworld   -->   HELLOWORLD   --> HELLOWORLD
        // 映射：java 8 中 Stream API :map() \reduce()
        var info1 = "hello".transform(info -> info + "world").transform(String::toUpperCase).transform(String::trim);
        System.out.println(info1);
    }

    @Test
    public void testTransform2() {
        System.out.println("======test java 12 transform======");
        List<String> list1 = List.of("Java", " Python", " C++ ");
        List<String> list2 = new ArrayList<>();
        list1.forEach(element -> list2.add(element.transform(String::strip)
                .transform(String::toUpperCase)
                .transform(e -> "Hi," + e))
        );
        list2.forEach(System.out::println);
    }

    @Test
    public void testTransform3() {
        // 如果使用Java 8的Stream特性
        System.out.println("======test java 12 transform======");
        List<String> list1 = List.of("Java", " Python", " C++ ");
        Stream<String> strStream = list1.stream().map(word -> word.strip()).map(String::toUpperCase).map(word -> "hello," + word);
        List<String> list2 = strStream.collect(Collectors.toList());
        list2.forEach(System.out::println);
    }


    @Test
    public void testIndent() {
        // String中的indent() 调整String实例的缩进
        // 换行符 \n 后向前缩进 n 个空格，为 0 或负数不缩进
        // 实质 调用了 lines() 方法来创建一个 Stream，然后再往前拼接指定数量的空格
        System.out.println("======test java 12 indent======");
        String result = "Java\n Python\nC++".indent(3);
        System.out.println(result);
    }
}
