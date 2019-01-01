# SpringBoot2.1 Features

[Spring-Boot-2.1-Release-Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes)

http://www.ityouknow.com/springboot/2018/11/03/spring-boot-2.1.html

## feature
* 将spring-boot-starter-oauth2-oidc-client重命名为spring-boot-starter-oauth2-client命名更简洁
* 添加 OAuth2 资源服务 starter，OAuth2 一个用于认证的组件。
* 支持 ConditionalOnBean 和 ConditionalOnMissingBean 下的参数化容器
* 自动配置 applicationTaskExecutor bean 的延迟加载来避免不必要的日志记录
* 将 DatabaseDriver＃SAP 重命名为 DatabaseDriver
* 跳过重启器不仅适用于 JUnit4，也适用于 JUnit5
* 在 Jest HealthIndicator 中使用集群端点
* 当 DevTools 禁用重启时添加日志输出
* 添加注解：@ConditionalOnMissingServletFilter提高 Servlet Filters 的自动装配

## 2.1 中的组件升级
* 升级 Hibernate 5.3，Hibernate 的支持升级到了 5.3
* 升级 Tomcat 9 ，支持最新的 tomcat 9
* 支持 Java 11，Java 现在更新越来越快，Spring 快赶不上了。
* 升级 Thymeleaf Extras Springsecurity 到 3.0.4.RELEASE ，thymeleaf-extras-springsecurity 是 Thymeleaf 提供集成 Spring Security 的组件
* 升级 Joda Time 2.10.1，Joda-Time， 面向 Java 应用程序的日期/时间库的替代选择，Joda-Time 令时间和日期值变得易于管理、操作和理解。
* 升级 Lettuce 5.1.2.RELEASE ，Lettuce 前面说过，传说中 Redis 最快的客户端。
* 升级 Reactor Californium-SR2 ，Californium 是物联网云服务的 Java COAP 实现。因此，它更专注的是可扩展性和可用性而不是像嵌入式设备那样关注资源效率。不过，Californium 也适合嵌入式的 JVM。
* 升级 Maven Failsafe Plugin 2.22.1 ，Maven 中的测试插件。
* 升级 Flyway 5.2.1 ， Flyway是一款开源的数据库版本管理工具
* 升级 Aspectj 1.9.2 ，AspectJ 是 Java 中流行的 AOP（Aspect-oriented Programming）编程扩展框架，是 Eclipse 托管给 Apache 基金会的一个开源项目。
* 升级 Mysql 8.0.13 ，Mysql 支持到 8。
* 升级 Undertow 2.0.14.Final ， Undertow 是一个用 java 编写的、灵活的、高性能的 Web 服务器，提供基于 NIO 的阻塞和非阻塞A PI。
* 升级 Rxjava2 2.2.3 ，RxJava是一个在 Java 虚拟机上的响应式扩展：一个用于通过使用可观察序列来编写异步和基于事件的程序的库。
* 升级 Hazelcast 3.11 ，Hazelcast 是一个开源的可嵌入式数据网格（社区版免费，企业版收费）。你可以把它看做是内存数据库，不过它与 Redis 等内存数据库又有些不同。
* 升级 Spring Kafka 2.2.0.RELEASE ，Kafka 高吞吐量、内置分区、支持数据副本和容错的 消息中间件。
* 升级 Spring Batch 4.1.0.RELEASE ，Spring 的批处理框架。
* 升级 Micrometer 1.1.0 ，Micrometer 是一款监控指标的度量类库，可以让您在没有供应商锁定的情况下对JVM 的应用程序代码进行调整。
* 升级 Spring Integration 5.1.0.RELEASE，Spring integration，它是一种便捷的事件驱动消息框架。
* 升级 Spring Data Lovelace SR2
* 升级 Spring Framework 5.1.2.RELEASE ，对于的 Spring 也进行了升级。
* 升级 Byte Buddy 1.9.3 ，Byte Buddy 是一个字节码生成与维护的库,主要用于在 Java 应用运行时生成和修改 Java 类,并且不需要编译器来辅助。
* 升级 Spring Session Bean-RELEASE ，Spring 提供 Session 管理的组件