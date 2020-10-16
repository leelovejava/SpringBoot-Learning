# JDK 15
JDK 15 is the open-source reference implementation of version 15 of the Java SE Platform, as specified by by JSR 390 in the Java Community Process.

JDK 15 reached General Availability on 15 September 2020. Production-ready binaries under the GPL are available from Oracle; binaries from other vendors will follow shortly.

The features and schedule of this release were proposed and tracked via the JEP Process, as amended by the JEP 2.0 proposal. The release was produced using the JDK Release Process (JEP 3).

## Features
339:	Edwards-Curve Digital Signature Algorithm (EdDSA)
360:	Sealed Classes (Preview)
371:	Hidden Classes
372:	Remove the Nashorn JavaScript Engine
373:	Reimplement the Legacy DatagramSocket API
374:	Disable and Deprecate Biased Locking
375:	Pattern Matching for instanceof (Second Preview)
377:	ZGC: A Scalable Low-Latency Garbage Collector
378:	Text Blocks
379:	Shenandoah: A Low-Pause-Time Garbage Collector
381:	Remove the Solaris and SPARC Ports
383:	Foreign-Memory Access API (Second Incubator)
384:	Records (Second Preview)
385:	Deprecate RMI Activation for Removal

## Schedule
2020/06/11		Rampdown Phase One (fork from main line)
2020/07/16		Rampdown Phase Two
2020/08/06		Initial Release Candidate
2020/08/20		Final Release Candidate
2020/09/15		General Availability

## 新特性（主菜）
* 01-密封类
* 02-隐藏类
* 03-instanceof模式匹配
* 04-ZGC功能转正
* 05-文本块功能转正
* 06-Records

## 新特性（配菜）
* 01-EdDSA 数字签名算法
* 02-重新实现 DatagramSocket API
* 03-禁用偏向锁定
* 04-Shenandoah 垃圾回收算法转正
* 05-外部存储器访问 API
* 06-移除 Solaris 和 SPARC 端口
* 07-移除the Nashorn JS引擎
* 08-废弃RMI 激活机制

## 5-新特性（饭后甜点）
* 01-添加项
* 02-移除项&废弃项
* 03-其他事项

### JEP 371:Hidden Classes(隐藏类)
     
该提案通过启用标准 API 来定义无法发现且具有有限生命周期的隐藏类，从而提高 JVM 上所有语言的效率。JDK内部和外部的框架将能够动态生成类，而这些类可以定义隐藏类。通常来说基于JVM的很多语言都有动态生成类的机制，这样可以提高语言的灵活性和效率。
* 隐藏类天生为框架设计的，在运行时生成内部的class。
* 隐藏类只能通过反射访问，不能直接被其他类的字节码访问。
* 隐藏类可以独立于其他类加载、卸载，这可以减少框架的内存占用。
     
#### Hidden Classes是什么呢？
Hidden Classes就是不能直接被其他class的二进制代码使用的class。
Hidden Classes主要被一些框架用来生成运行时类，但是这些类不是被用来直接使用的，而是通过反射机制来调用。
比如在JDK8中引入的lambda表达式，JVM并不会在编译的时候将lambda表达式转换成为专门的类，而是在运行时将相应的字节码动态生成相应的类对象。
另外使用动态代理也可以为某些类生成新的动态类。
     
那么我们希望这些动态生成的类需要具有什么特性呢？
* 不可发现性。因为我们是为某些静态的类动态生成的动态类，所以我们希望把这个动态生成的类看做是静态类的一部分。所以我们不希望除了该静态类之外的其他机制发现。
* 访问控制。我们希望在访问控制静态类的同时，也能控制到动态生成的类。
* 生命周期。动态生成类的生命周期一般都比较短，我们并不需要将其保存和静态类的生命周期一致。
     
API的支持
    所以我们需要一些API来定义无法发现的且具有有限生命周期的隐藏类。这将提高所有基于JVM的语言实现的效率。
比如：
    `java.lang.reflect.Proxy`可以定义隐藏类作为实现代理接口的代理类。
    `java.lang.invoke.StringConcatFactory`可以生成隐藏类来保存常量连接方法；
    `java.lang.invoke.LambdaMetaFactory`可以生成隐藏的nestmate类，以容纳访问封闭变量的lambda主体；
     
普通类是通过调用`ClassLoader::defineClass`创建的，而隐藏类是通过调用`Lookup::defineHiddenClass`创建的。

这使JVM从提供的字节中派生一个隐藏类，链接该隐藏类，并返回提供对隐藏类的反射访问的查找对象。

调用程序可以通过返回的查找对象来获取隐藏类的Class对象。

### JEP 377:ZGC: A Scalable Low-Latency Garbage Collector (Production) ZGC 功能转正
     
ZGC是Java 11引入的新的垃圾收集器（JDK9以后默认的垃圾回收器是G1），经过了多个实验阶段，自此终于成为正式特性。
自 2018 年以来，ZGC 已增加了许多改进，从并发类卸载、取消使用未使用的内存、对类数据共享的支持到改进的 NUMA 感知。此外，最大堆大小从 4 TB 增加到 16 TB。支持的平台包括 Linux、Windows 和 MacOS。
 
ZGC是一个重新设计的并发的垃圾回收器，通过减少 GC 停顿时间来提高性能。
 
但是这并不是替换默认的GC，默认的GC仍然还是G1；之前需要通过`-XX:+UnlockExperimentalVMOptions -XX:+UseZGC`来启用ZGC，现在只需要-XX:+UseZGC就可以。相信不久的将来它必将成为默认的垃圾回收器。
 
相关的参数有ZAllocationSpikeTolerance、ZCollectionInterval、ZFragmentationLimit、ZMarkStackSpaceLimit、ZProactive、ZUncommit、ZUncommitDelay ZGC-specific JFR events(ZAllocationStall、ZPageAllocation、ZPageCacheFlush、ZRelocationSet、ZRelocationSetGroup、ZUncommit)也从experimental变为product
     

     

