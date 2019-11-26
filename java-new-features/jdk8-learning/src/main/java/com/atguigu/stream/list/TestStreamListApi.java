package com.atguigu.stream.list;

import com.atguigu.stream.model.Employee;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 集合流
 *
 * @author y.x
 * @date 2019/11/25
 */
public class TestStreamListApi {
    /**
     * String转List
     *
     * @return
     */
    public List<String> StringToList() {
        String string = "文学,小说,历史,言情,科幻,悬疑";
        return Arrays.asList(string.split(",")).stream().map(s -> String.format(s.trim())).collect(Collectors.toList());
    }

    /**
     * List转String
     *
     * @return
     */
    public String ListToString() {
        List<String> list1 = Arrays.asList("文学", "小说", "历史", "言情", "科幻", "悬疑");

        List<String> list2 = Arrays.asList("文学", "小说", "历史", "言情", "科幻", "悬疑");

        //方案一：使用String.join()函数，给函数传递一个分隔符合一个迭代器，一个StringJoiner对象会帮助我们完成所有的事情
        String string1 = String.join(",", list1);

        System.out.println(string1);

        //方案二：采用流的方式来写
        String string2 = list2.stream().collect(Collectors.joining(","));

        return string2;
    }

    /**
     * list转map
     *
     * @param employees
     * @return
     */
    public Map<String, Employee> listToMap(List<Employee> employees) {
        if (employees.isEmpty()) {
            return null;
        }
        /** 去重策略,如果有多个相同的key,保留第一个*/
        return employees.stream().collect(Collectors.toMap(Employee::getName, m -> m, (k1, k2) -> k1));
    }

    /**
     * list中对象字段转一个list集合
     *
     * @param list
     * @return
     */
    public static List<String> getEmployeeNameList(List<Employee> list) {
        List<String> result = list.stream().map(x -> x.getName()).collect(Collectors.toList());
        for (String name : result) {
            System.out.println("name:" + name);
        }
        return result;
    }


    /**
     * list中对象字段转一个set集合
     *
     * @param list
     * @return
     */
    public static Set<String> getEmployeeNameSet(List<Employee> list) {
        Set<String> result = list.stream().map(student -> student.getName()).collect(Collectors.toSet());
        for (String name : result) {
            System.out.println("name:" + name);
        }
        return result;
    }


    /**
     * list 多字段排序,第一个字段降序,第二个字段升序
     */
    public void listSortBy() {

        List<Employee> list = initListData();
        System.out.println(list.toString());
        List<Employee> collect = list.stream().sorted(Comparator.comparing(Employee::getAge).reversed().thenComparing(Employee::getId)).collect(Collectors.toList());
        System.out.println("=======================");
        System.out.println(collect.toString());
    }

    /**
     * list分组,根据id分组
     */
    public void listGroupBy() {

        //List 以ID分组 Map<Integer,List<Employee>>
        List<Employee> list = initListData();
        Map<Integer, List<Employee>> groupBy = list.stream().collect(Collectors.groupingBy(Employee::getId));

        System.err.println("groupBy:" + groupBy);
        //{1=[Employee{id=1, name='苹果1', money=3.25, num=10}, Apple{id=1, name='苹果2', money=1.35, num=20}], 2=[Apple{id=2, name='香蕉', money=2.89, num=30}], 3=[Apple{id=3, name='荔枝', money=9.99, num=40}]}

    }

    public static List<Employee> initListData() {
        List<Employee> list = new ArrayList<>();
        Employee s1 = new Employee();
        s1.setAge(3);
        s1.setId(5);
        s1.setName("张三");
        Employee s2 = new Employee();
        s2.setAge(4);
        s2.setId(4);
        s2.setName("李四");
        Employee s3 = new Employee();
        s3.setAge(1);
        s3.setId(3);
        s3.setName("赵六");

        Employee s4 = new Employee();
        s4.setAge(8);
        s4.setId(7);
        s4.setName("田七");
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        return list;
    }
}
