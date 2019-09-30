package com.atguigu.function;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Test;

import com.atguigu.lambda.Employee;

/**
 * 函数式接口:只包含一个抽象方法的接口
 */
public class TestLambda {

    List<Employee> emps = Arrays.asList(
            new Employee(101, "张三", 18, 9999.99),
            new Employee(102, "李四", 59, 6666.66),
            new Employee(103, "王五", 28, 3333.33),
            new Employee(104, "赵六", 8, 7777.77),
            new Employee(105, "田七", 38, 5555.55)
    );

    @Test
    public void test1() {
        Collections.sort(emps, (e1, e2) -> {
            // 如果年龄相同，按姓名相比
            if (e1.getAge() == e2.getAge()) {
                return e1.getName().compareTo(e2.getName());
            } else {
                // - 倒过来排
                return -Integer.compare(e1.getAge(), e2.getAge());
            }
        });

        for (Employee emp : emps) {
            System.out.println(emp);
        }
    }

    @Test
    public void test2() {
        String trimStr = strHandler("\t\t\t 我大尚硅谷威武   ", (str) -> str.trim());
        System.out.println(trimStr);

        String upper = strHandler("abcdef", (str) -> str.toUpperCase());
        System.out.println(upper);

        String newStr = strHandler("我大尚硅谷威武", (str) -> str.substring(2, 5));
        System.out.println(newStr);
    }

    // 02 作为参数传递Lambda表达式
    //	需求：用于处理字符串
    public String strHandler(String str, MyFunction mf) {
        return mf.getValue(str);
    }

    @Test
    public void test3() {
        op(100L, 200L, (x, y) -> x + y);

        op(100L, 200L, (x, y) -> x * y);
    }

    //需求：对于两个 Long 型数据进行处理
    public void op(Long l1, Long l2, MyFunction2<Long, Long> mf) {
        System.out.println(mf.getValue(l1, l2));
    }

    ////////////内置函数式接口////////

    /**
     * Predicates
     * Predicates are boolean-valued functions of one argument. The interface contains various default methods for composing predicates to complex logical terms (and, or, negate)
     * 谓词
     * 谓词是只有一个参数的布尔型函数。这个接口包含不同的默认方法，将谓词组成复杂的逻辑组合
     */
    @Test
    public void test4() {
        Predicate<String> predicate = (s) -> s.length() > 0;

        // 检查给定参数的谓词
        Boolean a = predicate.test("foo");
        // true
        // 逻辑上相反的谓词
        Boolean b = predicate.negate().test("foo");
        // false

        // 引用静态方法
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        System.out.println(nonNull.test(null));
        // false
        System.out.println(isNull.test(null));
        // true

        // 引用普通方法
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        System.out.println(isEmpty.test(""));
        // true
        System.out.println(isNotEmpty.test(""));
        // false
    }

    /**
     * Functions
     * Functions accept one argument and produce a result. Default methods can be used to chain multiple functions together (compose, andThen).
     * 将Function对象应用到输入的参数上，然后返回计算结果
     */
    @Test
    public void test5() {
        // toInteger 称作函数对象
        // 表示一个方法，接受一个参数，产生一个结果；第一个是入参，第二个是结果
        Function<String, Integer> toInteger = Integer::valueOf;


        // 返回一个先执行当前函数对象apply方法再执行after函数对象apply方法的函数对象。
        Function<String, String> backToString = toInteger.andThen(String::valueOf);


        backToString.apply("123");
        // "123"


        // 这个函数式接口的函数方法是apply, 把函数应用在指定的参数上
        // 如果你想把接受一些输入参数并将对输入参数处理过后的结果返回的功能封装到一个方法内，Function接口是一个不错的选择
        System.out.println(toInteger.apply("123"));

        Function<String, Integer> f1 = (t) -> Integer.valueOf(t) * 10;
        System.out.println(f1.apply("3"));
        // 30 

        // 返回自身
        System.out.println(Function.identity().apply("3"));
        // 3

        // 先执行 apply 在执行andThen
        System.out.println(f1.andThen((r) -> String.valueOf(r) + ".....").apply("4"));
        // 40..... 

        // 先执行compose里面的函数 再执行本函数的apply
        System.out.println(f1.compose((String r) -> r.substring(1)).apply("a5"));
    }

    /**
     * Suppliers
     * Suppliers produce a result of a given generic type. Unlike Functions, Suppliers don't accept arguments.
     * supplier 产生指定类型的一个结果。不同于function，supplier 不接受参数。 类似 一个使用默认构造器的工厂方法
     */
    @Test
    public void test6() {
        Supplier<Employee> personSupplier = Employee::new;
        Employee employee = personSupplier.get();
        // new Employee
        System.out.println(employee);
        // Employee [id=0, name=null, age=0, salary=0.0]
    }

    /**
     * Consumers
     * Consumers represents operations to be performed on a single input argument.
     * consumer 表示作用在一个入参上的操作，没有返回值。例子 （星球大战）
     */
    @Test
    public void test7() {
        Consumer<Employee> greeter = (p) -> System.out.println("Hello, " + p.getName());
        greeter.accept(new Employee("Luke", 1));
    }

    /**
     * Comparators
     */
    @Test
    public void test8() {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.compareTo(a);
            }
        });

        Collections.sort(names, (String a, String b) -> {
            return b.compareTo(a);
        });

        Collections.sort(names, (String a, String b) -> b.compareTo(a));

        Comparator<Employee> comparator = (p1, p2) -> p1.getName().compareTo(p2.getName());

        Employee p1 = new Employee("John", 1);
        Employee p2 = new Employee("Alice", 2);

        comparator.compare(p1, p2);
        // > 0
        comparator.reversed().compare(p1, p2);
        // < 0
    }

    /**
     * Optional 看上去没什么用，和NPE相关的。参考 http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html
     * Optional 不是函数是接口，这是个用来防止NullPointerException异常的辅助类型，这是下一届中将要用到的重要概念，现在先简单的看看这个接口能干什么：
     * Optional 被定义为一个简单的容器，其值可能是null或者不是null。
     * 在Java 8之前一般某个函数应该返回非空对象但是偶尔却可能返回了null，而在Java 8中，不推荐你返回null而是返回Optional
     */
    @Test
    public void test9() {
        Optional<String> optional = Optional.of("bam");

        optional.isPresent();
        // true
        optional.get();
        // "bam"
        optional.orElse("fallback");
        // "bam"

        optional.ifPresent((s) -> System.out.println(s.charAt(0)));
        // "b"
    }
}
