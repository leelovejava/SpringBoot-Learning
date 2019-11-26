/**
 * 四.EasyRules规则引擎
 *
 * 1. 简介
 * easy-rules首先集成了mvel表达式，后续可能集成SpEL的一款轻量
 * 级规则引擎
 *
 * 2. 特性
 * easy rules是一个简单而强大的java规则引擎，它有以下特性：
 *
 * 轻量级框架，学习成本低
 * 基于POJO
 * 为定义业务引擎提供有用的抽象和简便的应用
 * 从原始的规则组合成复杂的规则
 * 它主要包括几个主要的类或接口：Rule,RulesEngine,RuleListener,Facts
 * 还有几个主要的注解：@Action,@Condition,@Fact,@Priority,@Rule
 *
 * 3. 使用方式
 * @Rule可以标注name和description属性，每个rule的name要唯一，
 * 如果没有指定，则RuleProxy则默认取类名
 * @Condition是条件判断，要求返回boolean值，表示是否满足条件
 *
 * @Action标注条件成立之后触发的方法
 *
 * @Priority标注该rule的优先级，默认是Integer.MAX_VALUE - 1，值
 * 越小越优先
 *
 * @Fact 我们要注意Facts的使用。Facts的用法很像Map，它是客户
 * 端和规则文件之间通信的桥梁。在客户端使用put方法向Facts中添
 * 加数据，在规则文件中通过key来得到相应的数据。
 *
 * 4. 使用方式
 * java
 * yaml
 *
 *  <dependency>
 *      <groupId>org.jeasy</groupId>
 *      <artifactId>easy-rules-core</artifactId>
 *      <version>3.1.0</version>
 *   </dependency>
 *   <dependency>
 *       <groupId>org.jeasy</groupId>
 *       <artifactId>easy-rules-mvel</artifactId>
 *       <version>3.1.0</version>
 *    </dependency>
 */
package com.leelovejava.drools.rules.easy;