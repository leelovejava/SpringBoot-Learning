/**
 * 1. Java JUC 简介
 * 2. volatile 关键字-内存可见性
 * 3. 原子变量-CAS算法
 * 4. ConcurrentHashMap 锁分段机制
 * 5. CountDownLatch 闭锁
 * 6. 实现 Callable 接口
 * 7. Lock 同步锁
 * 8. Condition 控制线程通信
 * 9. 线程按序交替
 * 10. ReadWriteLock 读写锁
 * 11. 线程八锁
 * 12. 线程池
 * 13. 线程调度
 * 14. ForkJoinPool 分支/合并框架 工作窃取
 * 15. 可重入锁 https://www.cnblogs.com/snowwhite/p/9508169.html
 */
package com.leelovejava.juc;
/**
 * 1) Java JUC 简介:
 * 在 Java 5.0 提供了 java.util.concurrent （简称JUC ）包，在此包中增加了在并发编程中很常用的实用工具类，
 * 用于定义类似于线程的自定义子系统，包括线程池、异步 IO 和轻量级任务框架。
 * 提供可调的、灵活的线程池。还提供了设计用于多线程上下文中的 Collection 实现等
 * 2) 三个概念: 原子性、可见性、有序性
 * 有序性：程序执行的顺序按照代码的先后顺序执行
 *
 * note:
 * https://gitee.com/moxi159753/LearningNotes/tree/master/%E6%A0%A1%E6%8B%9B%E9%9D%A2%E8%AF%95/JUC
 */
