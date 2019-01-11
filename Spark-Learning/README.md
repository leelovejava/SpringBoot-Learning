# Spark学习

## 目录
* sparkcore_accumulator    spark累加器
* sparkcore_partitioner    spark客户端分区
* sparkcore_wordcount      work统计
* sparkgraphx_helloworld   图计算入门案例
* sparksql-hive            sql的数据源之hive
* sparksql_helloworld      sql入门案例
* sparksql_safeMyAverage   sql自定义udf函数
* sparksql_unsafeMyAverage sql自定义聚合函数
* sparkstreaming_customerReceiver streaming之自定义接收器
* sparkstreaming_helloworld       streaming的入门案例
* sparkstreaming_kafka            streaming的数据源之kafka
* sparkstreaming_queueRdd         streaming的数据源之rdd队列
* sparkstreaming_statefulWordCount DStream的有状态转换操作之追踪状态变化
* sparkstreaming_windowWordCount   DStream的有状态转换操作之Window Operations
* structuredstreaming_wordCount    结构化流的统计


-------------------------

.
├── META-INF
│   └── MANIFEST.MF
├── SOFA-ARK
│   ├── biz
│   │   └── sofa-boot-demo-web-1.0-SNAPSHOT-sofa-ark-biz.jar
│   ├── container
│   │   ├── META-INF
│   │   │   └── MANIFEST.MF
│   │   ├── com
│   │   │   └── alipay
│   │   │       └── sofa
│   │   │           └── ark
│   │   └── lib
│   │       ├── aopalliance-1.0.jar
│   │       ├── guava-16.0.1.jar
│   │       ├── guice-4.0.jar
│   │       ├── guice-multibindings-4.0.jar
│   │       ├── javax.inject-1.jar
│   │       ├── log4j-1.2.17.jar
│   │       ├── slf4j-api-1.7.21.jar
│   │       ├── slf4j-log4j12-1.7.21.jar
│   │       ├── sofa-ark-archive-0.1.0.jar
│   │       ├── sofa-ark-common-0.1.0.jar
│   │       ├── sofa-ark-container-0.1.0.jar
│   │       ├── sofa-ark-exception-0.1.0.jar
│   │       ├── sofa-ark-spi-0.1.0.jar
│   │       └── sofa-common-tools-1.0.11.jar
│   └── plugin
│       └── sofa-ark-rpc-plugin-2.2.5-ark-plugin.jar
└── com
    └── alipay
        └── sofa
            └── ark
                ├── bootstrap
                │   ├── ArkLauncher.class
                │   ├── ClasspathLauncher$ClassPathArchive.class
                │   ├── ClasspathLauncher.class
                │   ├── ContainerClassLoader.class
                │   ├── EntryMethod.class
                │   ├── ExecutableArchiveLauncher.class
                │   ├── Launcher.class
                │   ├── MainMethodRunner.class
                │   └── SofaArkBootstrap.class
                ├── loader
                │   ├── DirectoryBizModuleArchive.class
                │   ├── ExecutableArkBizJar$1.class
                │   ├── ExecutableArkBizJar$2.class
                │   ├── ExecutableArkBizJar$3.class
                │   ├── ExecutableArkBizJar.class
                │   ├── JarBizModuleArchive$1.class
                │   ├── JarBizModuleArchive.class
                │   ├── JarContainerArchive$1.class
                │   ├── JarContainerArchive.class
                │   ├── JarPluginArchive$1.class
                │   ├── JarPluginArchive.class
                │   ├── archive
                │   ├── data
                │   └── jar
                └── spi
                    └── archive