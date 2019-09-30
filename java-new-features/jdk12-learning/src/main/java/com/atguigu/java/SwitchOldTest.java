package com.atguigu.java;

/**
 * 传统switch
 * @author shkstart
 * @create 2019 下午 3:06
 */
public class SwitchOldTest {
    /**
     * 传统switch的弊端
     * 传统的switch声明语句(switch statement)在使用中有一些问题：
     * 匹配是自上而下的，如果忘记写break, 后面的case语句不论匹配与否都会执行；
     * 所有的case语句共用一个块范围，在不同的case语句定义的变量名不能重复；
     * 不能在一个case里写多个执行结果一致的条件；
     * 整个switch不能作为表达式返回值；
     *
     * @param args
     */
    public static void main(String[] args) {
        int numberOfLetters;
        Fruit fruit = Fruit.APPLE;
        switch (fruit) {
            case PEAR:
//                int number = 10;
                numberOfLetters = 4;
                break;
            //case 穿透
            case APPLE:
            case GRAPE:
            case MANGO:
//                int number = 20;
                numberOfLetters = 5;
                break;
            // 错误的语法
//            case APPLE,GRAPE,MANGO:numberOfLetters = 5;
            case ORANGE:
            case PAPAYA:
                numberOfLetters = 6;
                break;
            default:
                throw new IllegalStateException("No Such Fruit:" + fruit);
        }
        System.out.println(numberOfLetters);

    }
}

enum Fruit {
    PEAR, APPLE, GRAPE, MANGO, ORANGE, PAPAYA;
}
