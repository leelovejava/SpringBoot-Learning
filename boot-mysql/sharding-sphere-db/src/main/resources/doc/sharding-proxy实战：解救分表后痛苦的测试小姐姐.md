## sharding-proxy实战：解救分表后痛苦的测试小姐姐

# 建议

根据你的业务特点，单表 > 分区 > 单库分表 > 分库分表，在满足业务前提下，优先级从左到右，不接受任何反驳。嘿嘿

# 背景

做过分表的（单库分表或者分库分表）都知道，在你没有依赖任何中间件之前，使用Navicat或者其他MySQL客户端工具操作MySQL那简直是一场灾难，如下图所示：

![sharding table view](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHnDX69b8uzErCDrEpg812cdP3LthpxrSgXKwwWtTSrcpuPnJr7bNpgPd4T9vWa7deZDIabNmcVGfA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)sharding table view

如果是类似取模这类简单算法分表还好说，能一眼根据分片键知道分表结，比如根据用户ID对128取模分表，那么用户ID为128的用户数据就在表tb_user_0中。但是如果是采用类似一致性hash算法或者更复杂的分表算法，那么我们首先需要利用程序根据分片键算出分表结果，然后再到Navicat中对该表进行CRUD。如果SQL条件中没有分片键，比如根据用户ID对用户表分表后，我要查询最新注册的10个用户，是不是崩溃了。对开发来说都如此困难，测试小姐姐你说她是不是会崩溃。哪里有困难，哪里就有机会。所以，这就是你和测试小姐姐拉近机会、表现自己的时候。

本文要介绍的就是能解决这个问题的工具：**sharding-proxy**（MyCAT也有类似工具，只是易用性简直就是渣渣），看看部署sharding-proxy后使用Navicat访问MySQL的效果图，开不开森，激不激动：

![image.png](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHnDX69b8uzErCDrEpg812cdBGdG5VfQv7VHddiaqzXtiadqGLIHBxbd4mjURDua3kViabzd1NpjYb6hA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)部署sharding-proxy前后

所以，sharding-proxy能带来什么呢？先说优点吧：

1. 完全屏蔽sharding细节，把它当做一张普通的表增删改查即可（分表算法集成在sharding-proxy的配置文件中，用户不需要care）
2. 打开数据库后，不再是看到满屏的表，而是只有几张简单的表（如上图左侧所示）。
3. 一个sharding-proxy可以代理多个数据源，测试只需要对接一个sharding-proxy即可。

再说缺点：

1. 无法操作"设计表"，即不能看到DDL。但是可以通过F6快捷键后show create table table_name查看表结果（以Navicat为例）；
2. sharding-sphere不支持的语法（非常有限），也不能在Navicat上操作。
3. 对MySQL客户端支持不是很全面（用Navicat访问时碰到"No database selected"，很可能就是Navicat的版本问题）。

是不是感觉很厉害的样子，OK，让我们花10分钟给测试小姐姐一个惊喜吧（部署sharding-proxy）。

# sharding-proxy

部署sharding-proxy非常简单，只需如下几个简单的步骤。另外，本次部署以sharding-proxy-3.0.0版本为例。

## 下载

下载地址：https://github.com/sharding-sphere/sharding-sphere-doc/raw/master/dist/sharding-proxy-3.0.0.tar.gz

下载后解压，我们看到只有简单的三个目录：lib目录就是sharding-proxy核心代码，以及依赖的JAR包；bin目录就是存放启停脚本的；conf目录就是存放所有配置文件，包括sharding-proxy服务的配置文件、数据源以及sharding规则配置文件和项目日志配置文件。

lib目录没什么好说的，就是sharding-proxy项目以及依赖的jar包。bin目录里面就是window或者Linux环境启停脚本。sharding-proxy启动的默认端口是3307，如果要自定义端口（以3308），执行`sh start.sh 3308`即可（window环境修改start.bat即可）。所有重要的配置都在conf目录下。

## 配置

- logback.xml

首先就是最简单的**日志配置**文件logback.xml，笔者对其简单的修改了一下，你可以任意自定义，这个没什么好说的，非常简单。

- server.xml

接下来就是与sharding-proxy**服务相关**的配置文件server.yaml，笔者的配置文件如下所示：

```properties
orchestration:
  name: orchestration_afei
  overwrite: true
  registry:
    # zk地址，建议集群
    serverLists: 127.0.0.1:2181
    namespace: orchestration_afei

# 用户通过Navicat访问sharding-proxy的用户名密码
authentication:
  username: afei
  password: afei

# sharding-proxy相关配置，建议sql.show设置为true，方便定位问题
props:
  max.connections.size.per.query: 1
  acceptor.size: 16 
  executor.size: 16 
  proxy.transaction.enabled: false
  proxy.opentracing.enabled: false
  sql.show: true
```



- config.xml

接下来就是最重要的**数据源以及sharding规则配置**文件（config.xml）了。需要说明的是，**一个sharding-proxy实例能支持多个数据源**。

配置参考（说明：afei这个库中，tb_afei和afei_fenbiao都有分表，其他表都不需要分库分表）：

```properties
schemaName: afeidb

dataSources:
  afeidb:
    url: jdbc:mysql://172.0.0.1:3306/afeidb?serverTimezone=UTC&useSSL=false
    username: afei
    password: afei
    autoCommit: true
    connectionTimeout: 10000
    idleTimeout: 60000
    maxLifetime: 1800000
    maximumPoolSize: 50

shardingRule:
  tables:
    # tb_afei的分表算法是根据trade_id对128取模
    tb_afei:
      actualDataNodes: afeidb.tb_afei_${0..127}
      tableStrategy:
        inline:
          shardingColumn: trade_id
          algorithmExpression: tb_afei_${trade_id % 128}    
    # afei_fenbiao的分表算法是在AfeiFenbiaoShardingAlgorithm中自定义      
    afei_fenbiao:
      actualDataNodes: afeidb.afei_fenbiao_${0..127}
      tableStrategy:
        standard:
          shardingColumn: id
          preciseAlgorithmClassName: com.afei.sjdbc.proxy.AfeiFenbiaoShardingAlgorithm
  bindingTables:

  # 默认数据库没有分的策略
  defaultDatabaseStrategy:
    none:
  # 默认表没有分的策略
  defaultTableStrategy:
    none:
  defaultKeyGeneratorClassName: io.shardingsphere.core.keygen.DefaultKeyGenerator
```

> 说明：sharding-proxy的配置文件是yaml格式，其语法非常讲究：不能用Tab键，和python一样对空格有严格的要求，错误提示也极其不友好。但是，它的优点也很多，笔者以后单独撰文说明。所以，在你修改这些yaml配置文件的时候，强烈建议你先借助IDE工具编辑并验证OK后（参考校验地址：http://www.bejson.com/validators/yaml_editor/），再复制到sharding-proxy的conf目录下，而不是使用对yaml非常不友好的普通文本编辑器。

如上配置所示，afei_fenbiao表需要的自定义分表算法定义在AfeiFenbiaoShardingAlgorithm.java中，我们只需要将它编译成class文件，然后放到conf目录下即可（如果自定义算法涉及的文件比较多，可以将其打成jar包放到lib目录下），参考目录结构：

```
com
  ├── afei/sjdbc/proxy/AfeiFenbiaoShardingAlgorithm.class
config.yaml
logback.xml
server.yaml
```



- 自定义分表算法源码

afei_fenbiao表使用的自定义分片算法AfeiFenbiaoShardingAlgorithm.java源码如下：

```java
@Slf4j
public class AfeiFenbiaoShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    private static final int SHARDING_COUNT = 128;

    private static final String TABLE_NAME = "afei_fenbiao_";

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<String> shardingValue) {
        String hashCode = String.valueOf(shardingValue.getValue().hashCode());
        long mod = Math.abs(Long.parseLong(hashCode)) % SHARDING_COUNT;
        for (String each : availableTargetNames) {
            if (each.equals( TABLE_NAME + mod )) {
                return each;
            }
        }
        log.error("The sharding rule is ERROR !!! mod: {}, sharding value: {}", mod, shardingValue.getValue());
        throw new UnsupportedOperationException();
    }

}
```



- Navicat

接下来只需要让测试小姐姐用Navicat连接上sharding-proxy即可，至于分库分表这些细节本来就应该对用户是屏蔽的，是不是测试小姐姐会感觉你棒棒哒：

![Navicat connect sharding proxy](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHnDX69b8uzErCDrEpg812cdlgtLRywAkg3NoFokQ25W00p6AWlVEbDwxMAVy8YfMImHXPddFWEZCg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)