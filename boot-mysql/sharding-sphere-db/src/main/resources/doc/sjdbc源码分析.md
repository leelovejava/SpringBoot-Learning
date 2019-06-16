## sjdbc源码分析

## 0. sjdbc源码分析准备工作

接下来对sharding-jdbc的源码分析基于tag为`2.0.3`源码，如果想了解shardin-jdbc 1.x版本，可以查看我的简书shading-jdbc源码分析系列：https://www.jianshu.com/nb/21558552；

### 概况

sharding-jdbc2.x源码主要有以下几个模块：**sharding-jdbc-core**、**sharding-jdbc-core-spring**、**sharding-jdbc-orchestration**、**sharding-jdbc-orchestration-spring**、**sharding-jdbc-plugin**、**sharding-jdbc-transaction-parent**；由模块命名很容易知道模块的作用：

- sharding-jdbc-core：核心源码；
- sharding-jdbc-core-spring：有两个子模块sharding-jdbc-core-spring-namespace（通过spring集成相关代码）和sharding-jdbc-core-spring-boot-starter（通过spring-boot集成相关代码）；
- sharding-jdbc-orchestration：服务编排相关代码；
- sharding-jdbc-orchestration-spring：如果通过spring集成服务编排的相关源码；
- sharding-jdbc-transaction-parent：事务相关源码；

sharding-jdbc源码结构：

```
sharding-jdbc
    ├──sharding-jdbc-core                          分库分表、读写分离核心模块，可直接使用
    ├──sharding-jdbc-core-spring                   Spring配置父模块，不应直接使用
    ├      ├──sharding-jdbc-core-spring-namespace  Spring命名空间支持模块，可直接使用
    ├      ├──sharding-jdbc-core-spring-boot       SpringBoot支持模块，可直接使用
    ├──sharding-jdbc-orchestration                 数据库服务编排治理模块，可接使用
    ├──sharding-jdbc-transaction-parent            柔性事务父模块，不应直接使用
    ├      ├──sharding-jdbc-transaction            柔性事务核心模块，可直接使用
    ├      ├──sharding-jdbc-transaction-storage    柔性事务存储模块，不应直接使用
    ├      ├──sharding-jdbc-transaction-async-job  柔性事务异步作业，不应直接使用，直接下载tar包配置启动即可
    ├──sharding-jdbc-plugin                        插件模块，目前包含自定义分布式自增主键，可直接使用
```

**服务编排**：
2.0.0.M1版本开始，Sharding-JDBC提供了数据库治理功能，即服务编排，主要包括：

- 配置集中化与动态化，可支持数据源、表与分片及读写分离策略的动态切换
- 数据治理。提供熔断数据库访问程序对数据库的访问和禁用从库的访问的能力
- 支持Zookeeper和Etcd的注册中心

### 依赖的技术栈

1. **lombok**（能够简化java的代码，不需要显示编写setter，getter，constructor等代码，其原理是作用于javac阶段。我们可以通过反编译class文件看到还是有通过lombok省略掉的setter，getter，constructor等相关代码）
2. **google-guava**（google开源的google使用的Java核心类）
3. **elastic-job**（sharding-jdbc-transaction-async-job模块依赖elastic-job实现分布式定时任务）
4. **inline表达式**（sharding-jdbc分库分表规则表达式采用inline语法，可以参考InlineBasicExpressionParserTest.java这个测试类中一些基本的inline表达式）

> 在阅读sharding-jdbc源码之前，建议对这些技术有一定的了解，从而在接下来的学习中事半功倍；

### 架构图

![sharding-jdbc架构图](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHkJbaYobD9DDoD2ibEaDTbYe3ibtPqfcHn0bibCfAJrVF0maOUdz5jNib9tOXKLWzfpeDaHlZhMerlJIw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)sharding-jdbc架构图

> 说明：图片来源于sharding-jdbc官网

### 官方文档

官方文档请戳链接：http://shardingjdbc.io/document/legacy/2.x/cn/00-overview/，**强烈建议**在阅读源码之前，先阅读一遍官方文档，对其原理，一些核心概念，以及初级用法有一个大概的了解。这样的话，接下来的源码深入分析会轻松很多；

### 实现剖析

接下来的源码分析文章大概分析（但不局限于）sharding-jdbc的这些核心功能；

1. 分片规则配置说明
2. SQL解析
3. SQL路由
4. SQL改写
5. SQL执行
6. 结果归并
7. 服务编排

### Debug

学习开源组件的最好的办法就是了解它的大概原理后，下载它的源码，然后Run起来；sharding-jdbc的测试用例写的非常优秀，每个模块的test目录下，都有大量的用例代码（笔者大概统计了一下，核心代码和用例代码几乎能达到`1:1`）。如果你刚刚下载了源码并导入IDE后，会发现很多编译错误。事实上这不是源码问题，而是需要先来一步骚气的操作**在sharding-jdbc-orchestration模块下执行mvn install命令**：

> 这一步操作会根据protobuf文件生成gRPC相关的java文件。官方文章的FAQ有说明：http://shardingjdbc.io/document/legacy/2.x/cn/01-start/faq/

接下来准备运行一个比较核心的用例`--`sharding-jdbc-core模块中的`AllCoreTests.java`，包含了多个关键测试用例合集，如果这个用例运行一切OK，表示你的源码环境已经OK。

> 说明：由于sharding-jdbc测试用例以`H2`为数据库。所以，我们运行用例前，不需要搭建MySQL或者其他数据库环境。这点比起sharding-jdbc1.x要方便很多。

## 1. sjdbc2.0.3集成--基于spring-boot

本篇文章讲解如何用spring-boot集成sharding-jdbc（版本为**2.0.3**）进行分库分表，假设分库分表行为如下：

- 将t_user表分到2个库（afei_user_0~afei_user_1）中；
- 其他表不进行分库分表，保留在afei_test库中；

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJgrMt1SM4hThnHlfu2DxMxCdgdVkVpmMUYxSR7kJWFGe1ySUuDaAXeQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

### 1.POM配置

```
<!--如果通过springboot方式集成, 需要增加如下maven坐标, 且其已经依赖了sharding-jdbc-core-->
<dependency>
    <groupId>io.shardingjdbc</groupId>
    <artifactId>sharding-jdbc-core-spring-boot-starter</artifactId>
    <version>2.0.3</version>
</dependency>
<!--springboot核心依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>1.5.15.RELEASE</version>
</dependency>
```

spring集成mybatis访问mysql，且以dbcp为数据源其他需要的依赖有：

- commons-dbcp.commons-dbcp（1.4）
- org.mybatis.mybatis-spring（1.3.1）
- org.mybatis.mybatis（3.4.5）
- mysql.mysql-connector-java（5.1.39）
- org.springframework.spring-jdbc（4.3.13.RELEASE）
- ch.qos.logback.logback-classic（1.1.11）
- org.projectlombok.lombok（1.16.4，up to you）
- org.springframework.boot.spring-boot-starter-test（1.5.15.RELEASE）
- junit.junit（4.12）

### 2.核心配置文件

springboot项目的核心配置文件是**application.properties**，至于sharding-jdbc项目这个文件如何定义，可以参考sharding-jdbc源码中sharding-jdbc-core-spring-boot-starter模块下的application-sharding.properties（如果是主从模式，参考application-masterslave.properties），适配这次分库分表规则，配置文件定义如下：

```
# 枚举所有数据源
sharding.jdbc.datasource.names=afei_test,afei_user_0,afei_user_1

# afei_test这个数据源的配置
sharding.jdbc.datasource.afei_test.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.afei_test.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.afei_test.url=jdbc:mysql://10.0.0.1:3306/afei_test
sharding.jdbc.datasource.afei_test.username=afei
sharding.jdbc.datasource.afei_test.password=afei123
sharding.jdbc.datasource.afei_test.maxActive=16

# afei_user_0这个数据源的配置
sharding.jdbc.datasource.afei_user_0.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.afei_user_0.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.afei_user_0.url=jdbc:mysql://10.0.0.1:3306/afei_user_0
sharding.jdbc.datasource.afei_user_0.username=afei
sharding.jdbc.datasource.afei_user_0.password=afei123
sharding.jdbc.datasource.afei_user_0.max-active=16

# afei_user_1这个数据源的配置
sharding.jdbc.datasource.afei_user_1.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.afei_user_1.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.afei_user_1.url=jdbc:mysql://10.0.0.1:3306/afei_user_1
sharding.jdbc.datasource.afei_user_1.username=afei
sharding.jdbc.datasource.afei_user_1.password=afei123
sharding.jdbc.datasource.afei_user_1.max-active=16

# 默认数据源名称
sharding.jdbc.config.sharding.default-data-source-name=afei_test
# 默认数据源不用分库分表，所以不需要配置sharding.jdbc.config.sharding.default-database-strategy...

# 指定t_user表分库分表的inline表达式--由表达式可知t_user表如何分库分表的
sharding.jdbc.config.sharding.tables.t_user.actual-data-nodes=afei_user_${0..1}.t_user

# 指定两个属性的值（sql.show=true, executor.size=100）
sharding.jdbc.config.sharding.props.sql.show=true
sharding.jdbc.config.sharding.props.executor.size=100
```

### 3.启动入口

springboot项目测试用例代码如下：

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SprintBootTest.class)
@SpringBootApplication
@ActiveProfiles("sharding")
public class SprintBootTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConfigMapper configMapper;

    @Test
    public void test(){
        // t_user有进行分库，且id为1时路由到afei_user_1数据库
        User authUser = userMapper.selectByPrimaryKey(1L);
        Assert.assertEquals((long)authUser.getId(), 1L);
        System.out.println(authUser);

        // t_user有进行分库，且id为2时路由到afei_user_0数据库
        authUser = userMapper.selectByPrimaryKey(2L);
        Assert.assertEquals((long)authUser.getId(), 2L);
        System.out.println(authUser);

        // t_config没有分库分表，走默认数据源，即afei_test数据源
        Config config = configMapper.selectByPrimaryKey(1L);
        Assert.assertEquals((long)config.getId(), 1L);
        System.out.println(config);
    }
}
```

> 这个类的编写可以参考sharding-jdbc源码中的SpringBootShardingTest.java源代码，并且项目实际使用时就可以参考这个测试类。

### 4.集成mybatis

由于使用springboot方式集成sharding-jdbc，所以mybatis也用springboot方式集成，主要有两个核心类。

```
@Configuration
@EnableTransactionManagement
public class MyBatisConfig implements TransactionManagementConfigurer {

    //集成sharding-jdbc分表数据源
    @Resource
    DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //mybatis访问数据库的实体对应包的路径
        bean.setTypeAliasesPackage("com.afei.sjdbc.model");
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // mybatis的mapper文件路径
            bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
@Configuration
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        //mapper类路径
        mapperScannerConfigurer.setBasePackage("com.afei.sjdbc.mapper");
        return mapperScannerConfigurer;
    }

}
```

### 5.非核心代码

mapper里的ConfigMapper.xml，UserMapper.xml，model里的Config.java，User.java，mapper里的ConfigMapper.java，UserMapper.java两个接口。这些代码和平常的方式完全一样。只是如果引入了`lombok`，那么User.java和Config.java两个model可以简化代码：

```
@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
    private Integer id;
    private String username;
    private String password;

}
```

> @Getter和@Setter省略了属性的setter和getter方法相关代码，@ToString省略了toString()方法相关代码，@AllArgsConstructor省略了带所有属性的构造方法相关代码。@NoArgsConstructor省略了空参构造方法相关代码。

- idea集成lombok说明

开发工具idea集成lombok后，编译可能会报错：找不到符号getter方法，或者setter方法。解决办法如下：
Settings->Build, Execution, Deployment->Compiler->Annotation Processors中勾选**"Enable annotation processing"**

### 6.源码结构图

![springboot集成sjdbc分库分表demo目录结构图](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHmsdiaEchfayU4EPBiaQsAS21nXaAIrSc2VE4oBa7IahhkyU8wRu8buKVSgHUISLSjDiapicWkCwfric0g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)springboot集成sjdbc分库分表demo目录结构图

# sharding-jdbc-core-spring-boot-starter模块源码分析

这个模块的源码量非常少，源码结构如下：

```
sharding-jdbc-core-spring-boot-starter
    ├──main/java/io.shardingjdbc.spring.boot                 
    ├      ├──masterslave  
    ├      ├      ├──SpringBootMasterSlaveRuleConfigurationProperties.java  sjdbc主从模式集成配置
    ├      ├──sharding      
    ├      ├      ├──SpringBootShardingRuleConfigurationProperties.java     sjdbc普通模式集成配置
    ├──resources/META-INF           
    ├      ├──additional-spring-configuration-metadata.json                 springboot元数据文件           
    ├      ├──spring.factories                                              指定springboot集成入口, 最核心   
    ├      ├──spring.provides                                               当前spring-boot-starter的名称                     
```

### metadata.json

全名是additional-spring-configuration-metadata.json，这个文件的术语在spring boot里叫做metadata即元数据。Spring Boot jar包含元数据文件提供所有支持的配置属性的详细信息。这些文件旨在允许IDE开发人员在用户使用application.properties 或application.yml文件时提供上下文帮助和"代码完成"。主要的元数据文件是在编译器通过处理所有被@ConfigurationProperties注解的节点来自动生成的。配置元数据位于jars文件中的META-INF/spring-configuration-metadata.json，它们使用一个具有"groups”或”properties”分类节点的简单JSON格式。

### spring.factories

sjdbc源码中这个文件内容如下：

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
io.shardingjdbc.spring.boot.SpringBootConfiguration
```

阅读spring.factories这是阅读所有spring-boot-starter的第一步。因为@EnableAutoConfiguration自动配置的原理其实是：从classpath中搜寻所有META-INF/spring.factories配置文件，并将其中org.spring.framework.boot.autoconfigure.EnableAutoConfiguration对应的配置项通过反射(Java Reflection)实例化为对应的标注了@Configration的JavaConfig形式的IoC容器配置类，然后汇总为一个并加载到IoC容器。

> 说明：@SpringBootApplication注解间接加了@EnableAutoConfiguration注解。

### SpringBootConfiguration.java

自定义spring-boot-start的入口，由spring.factories定义，这个核心类的源码如下：

```
@Configuration
@EnableConfigurationProperties({SpringBootShardingRuleConfigurationProperties.class, SpringBootMasterSlaveRuleConfigurationProperties.class})
public class SpringBootConfiguration implements EnvironmentAware {

    @Autowired
    private SpringBootShardingRuleConfigurationProperties shardingProperties;

    @Autowired
    private SpringBootMasterSlaveRuleConfigurationProperties masterSlaveProperties;

    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    @Bean
    public DataSource dataSource() throws SQLException {
        return null == masterSlaveProperties.getMasterDataSourceName() 
                ? ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingProperties.getShardingRuleConfiguration(), shardingProperties.getConfigMap(), shardingProperties.getProps())
                : MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveProperties.getMasterSlaveRuleConfiguration(), masterSlaveProperties.getConfigMap());
    }

    @Override
    public void setEnvironment(final Environment environment) {
        // environment是springboot环境配置，包括了application.properties属性配置
        setDataSourceMap(environment);
    }

    private void setDataSourceMap(final Environment environment) {
        // 从springboot配置中获取"sharding.jdbc.datasource."前缀的属性（sharding-jdbc约定的数据源配置前缀）
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "sharding.jdbc.datasource.");
        // 获取sharding.jdbc.datasource.names的值(sharding.jdbc.datasource.前缀下获取names)，我的集成demo中这个属性的值为afei_test,afei_user_0,afei_user_1，即所有数据源的命名
        String dataSources = propertyResolver.getProperty("names");
        // 遍历这些数据源
        for (String each : dataSources.split(",")) {
            try {
                // 分别得到sharding.jdbc.datasource.${数据源名称}的属性集合
                Map<String, Object> dataSourceProps = propertyResolver.getSubProperties(each + ".");
                // 每个数据源的配置信息不允许为空
                Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
                // 根据数据源的配置信息构造DataSource
                DataSource dataSource = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
                dataSourceMap.put(each, dataSource);
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingJdbcException("Can't find datasource type!", ex);
            }
        }
    }
}
```

接下来我们把前面demo里的application.properties文件里的属性分类：

1. sharding.jdbc.datasource.names -- 由上面对SpringBootConfiguration.java源码分析可知，这个属性的命名由其决定。列举所有数据源名称，每个数据源之间以逗号隔开。
2. sharding.jdbc.datasource.[name] -- sharding.jdbc.datasource.[name]的命名由SpringBootConfiguration.java决定，后面带什么属性，比如：driver-class-name, url, username, password等，由具体数据源的属性决定，且复杂的属性命名方式为：单词小写，以中划线隔开。例如BasicDataSource.java数据源有属性testWhileIdle，那么在application.properties中这个属性的完整命名为：sharding.jdbc.datasource.[name].test-while-idle（映射逻辑原理可以通过sharding-jdbc中DataSourceUtil.java的源码可知）。
3. sharding.jdbc.config.sharding -- 这个命名前缀由SpringBootShardingRuleConfigurationProperties.java的注解申明决定，那么它后面的属性就由它父类的属性决定，例如YamlShardingRuleConfiguration.java中有defaultDataSourceName，那么这个属性在application.properties中对应的命名为：sharding.jdbc.config.sharding.default-data-source-name：

```
@ConfigurationProperties(prefix = "sharding.jdbc.config.sharding")
public class SpringBootShardingRuleConfigurationProperties extends YamlShardingRuleConfiguration {
}
```

1. sharding.jdbc.config.masterslave --
   这个命名前缀由SpringBootMasterSlaveRuleConfigurationProperties.java的注解申明决定，那么它后面的属性就由它父类的属性决定，例如YamlMasterSlaveRuleConfiguration.java中有masterDataSourceName，那么这个属性在application.properties中对应的命名为：sharding.jdbc.config.masterslave.master-data-source-name：

```
@ConfigurationProperties(prefix = "sharding.jdbc.config.masterslave")
public class SpringBootMasterSlaveRuleConfigurationProperties extends YamlMasterSlaveRuleConfiguration {
}
```

到这里，sharding-jdbc以spring-boot集成的原理就分析完了，是不是很简单^^



## 3. sjdbc源码之词法分析

词法分析结果词法分析源码1.LexerEngine2.跳过忽略的token3.获取tokenSQL解析器

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJS6eOxSPTwKFzSyHgTWZptiaHd9lW2tmaKNKduSrPlnoVBxmVDZMr6eg/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

sharding-jdbc对SQL解析的源码主要在sharding-jdbc-core模块的`io.shardingjdbc.core.parsing`目录下，通过源码结构可知，SQL解析主要有两个部分：lexer和parser。lexer就是本文需要分析的词法分析（parser即SQL解析下文再讲）：

```
sharding-jdbc-core
    ├──main/java/io.shardingjdbc.core.parsing                 
    ├      ├──lexer  
    ├      ├      ├──analyzer  分析器（对SQL进行分析的地方）
    ├      ├      ├──dialect   数据库方言（包括mysql，oracle，postgresql，sqlserver）
    ├      ├      ├──token     词法分析得到的表征
    ├      ├      ├──Lexer.java  词法分析器
    ├      ├      ├──LexerEngine.java  词法分析器引擎
    ├      ├      ├──LexerEngineFactory.java  词法分析器引擎工厂类，不同的数据库，构造不同的词法分析器，例如：new LexerEngine(new MySQLLexer(sql)) 
    ├      ├──parser
    ├      ├      ├──... ...             
```

### 词法分析结果

分析sharding-jdbc的词法分析源码之前，先大概说一下词法分析是干嘛的，后面理解起来就会更容易，例如对于SQL：**/\*! hello, afei \*/select from t_user where user_id=?**而言，词法分析结果如下：

| SQL                | Token                                               |
| :----------------- | :-------------------------------------------------- |
| /*! hello, afei */ | 跳过                                                |
| select             | (type=SELECT, literals=select, endPosition=24)      |
| from               | (type=FROM, literals=from, endPosition=29)          |
| t_user             | (type=IDENTIFIER, literals=t_user, endPosition=36)  |
| where              | (type=WHERE, literals=where, endPosition=42)        |
| user_id            | (type=IDENTIFIER, literals=user_id, endPosition=50) |
| =                  | (type=EQ, literals==, endPosition=51)               |
| ?                  | (type=QUESTION, literals=?, endPosition=52)         |
|                    | (type=END, literals=, endPosition=52)               |

> 对SQL词法分析后，才能进行SQL解析。

得到上面的词法分析结果很简单，只需要在TokenTest.java中自定义一个测试用例：

```
@Test
public void assertDelete() {
    // 定义你需要分析的SQL
    String sql = "/*! hello, afei */select from t_user where user_id=?";
    Lexer lexer = new Lexer(sql, dictionary);
    while(true) {
        lexer.nextToken();
        Token currentToken = lexer.getCurrentToken();
        System.out.println("Current token: "+currentToken);
        if (currentToken.getType() == Assist.END){
            break;
        }
    }
}
```

我们也可以参考其他用例，大概了解一下词法分析结果。例如：

```
@Test(expected = SQLParsingException.class)
public void assertSyntaxErrorForUnclosedChar() {
    Lexer lexer = new Lexer("UPDATE product p SET p.title='Title's',s.description='中文' WHERE p.product_id=?", dictionary);
    LexerAssert.assertNextToken(lexer, DefaultKeyword.UPDATE, "UPDATE");
    LexerAssert.assertNextToken(lexer, Literals.IDENTIFIER, "product");
    LexerAssert.assertNextToken(lexer, Literals.IDENTIFIER, "p");
    LexerAssert.assertNextToken(lexer, DefaultKeyword.SET, "SET");
    LexerAssert.assertNextToken(lexer, Literals.IDENTIFIER, "p");
    LexerAssert.assertNextToken(lexer, Symbol.DOT, ".");
    LexerAssert.assertNextToken(lexer, Literals.IDENTIFIER, "title");
    LexerAssert.assertNextToken(lexer, Symbol.EQ, "=");
    LexerAssert.assertNextToken(lexer, Literals.CHARS, "Title");
    LexerAssert.assertNextToken(lexer, Literals.IDENTIFIER, "s");
    LexerAssert.assertNextToken(lexer, Literals.CHARS, ",s.description=");
    lexer.nextToken();
}
```

> 由词法分析结果可知，整个词法分析过程大概就是遍历整个SQL，将其一一解析为Token，直到解析到SQL结束为止。

### 词法分析源码

根据`SQLParsingEngine.java`源码可知，通过`SQLParserFactory`构造SQL解析器之前需要先得到`LexerEngine`，即词法分析器引擎：

```
@RequiredArgsConstructor
public final class SQLParsingEngine {

    // 数据库类型，例如MySQL，Oracle等
    private final DatabaseType dbType;

    // 原SQL（解析前的SQL）
    private final String sql;

    // sharding分库分表规则
    private final ShardingRule shardingRule;

    public SQLStatement parse() {
    LexerEngine lexerEngine = LexerEngineFactory.newInstance(dbType, sql);
    lexerEngine.nextToken();
    return SQLParserFactory.newInstance(dbType, lexerEngine.getCurrentToken().getType(), shardingRule, lexerEngine).parse();
}
```

> 从parse()方法的结果可知，SQL解析的结果就是SQLStatement，上面的测试用例是SELECT类型的SQL，即SelectStatement，是SQLStatement的子类，类关系如下图所示：

#### 1.LexerEngine

所以，SQL解析的第一步就是得到词法分析器，对应源码为`LexerEngine lexerEngine = LexerEngineFactory.newInstance(dbType, sql);`，核心实现源码如下：

```
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LexerEngineFactory {

    public static LexerEngine newInstance(final DatabaseType dbType, final String sql) {
        // 不同的数据库类型获取LexerEngine不一样（因为语法不一样，关键词也不一样）
        switch (dbType) {
            case H2:
            case MySQL:
                return new LexerEngine(new MySQLLexer(sql));
            case Oracle:
                return new LexerEngine(new OracleLexer(sql));
            case SQLServer:
                return new LexerEngine(new SQLServerLexer(sql));
            case PostgreSQL:
                return new LexerEngine(new PostgreSQLLexer(sql));
            default:
                throw new UnsupportedOperationException(String.format("Cannot support database [%s].", dbType));
        }
    }
}
```

> 从这里可知，sharding-jdbc只支持这些数据库：H2，MySQL，Oracle，SQLServer，PostgreSQL，其他数据库如DB2是不支持的，否则会抛出异常：`Cannot support database DB2`。并且从这里可知H2和MySQL的解析完全一致，所以sharding-jdbc2.x的测试用例都依赖H2数据库，这样的话，测试用例成本更低，不需要依赖外部中间件。

接下来的分析以Mysql为例，获取MySQLLexer源码如下，可知Lexer两个主要属性为sql和关键词字典：

```
// 词法分析器的这几个属性比较重要，贯穿整个SQL解析过程
public class Lexer {

    // 待解析的SQL语句，例如"/*! hello, afei */select from t_user where user_id=?"
    @Getter
    private final String input;

    // 字典属性下面有解析，主要包括数据库对应的所有关键词名称和关键词的map映射
    private final Dictionary dictionary;

    // 解析SQL的位置偏移量
    private int offset;

    // 当前解析的token
    @Getter
    private Token currentToken;

    ... ...
}

public final class MySQLLexer extends Lexer {

    // MySQL词法分析器核心Dictionary的构造方法入参为MySQL所有关键词合集（可以预见各数据库的词法分析器都包含其所有SQL关键词信息），MySQL所有关键词集合包括MySQLKeyword和DefaultKeyword；其他数据库类似，例如Oracle所有关键词集合包括OracleKeyword和DefaultKeyword    
    private static Dictionary dictionary = new Dictionary(MySQLKeyword.values());

    public MySQLLexer(final String input) {
        super(input, dictionary);
    }
    ... ...
```

> 通过Dictionary.java的构造方法调用的fill()方法可知，Dictionary的核心属性`Map<String, Keyword> tokens`包含了DefaultKeyword和数据库方言Keyword两部分（mysql数据库就是MySQLKeyword）。

#### 2.跳过忽略的token

对应的核心实现源码如下（Lexer.java）：

```
// 跳过忽略的token
private void skipIgnoredToken() {
    offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
    while (isHintBegin()) {
        offset = new Tokenizer(input, dictionary, offset).skipHint();
        offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
    }
    while (isCommentBegin()) {
        offset = new Tokenizer(input, dictionary, offset).skipComment();
        offset = new Tokenizer(input, dictionary, offset).skipWhitespace();
    }
}
```

由这段代码中方法skipIgnoredToken()可知，忽略的token主要包括：

- **空格**，如下图所示：

  ![SQL中的空格](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHkC0jcN0wxwfxdrmWAwSg64ZfGhyic5jPgApGIeMMpwQXv0Go6y9LATURrMWy9mU0vPwwN2S50U11w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)SQL中的空格

- **hint与后面的空格**，例如MySQL的hint语法为`/*! hint something*/`，`"SELECT /*!40001 SQL_CACHE */ o.* FROM t_order o where o.order_id=?"`这条SQL有hint`/*!40001 SQL_CACHE */`；（Oracle的hint语法有所不同，通过OracleLexer.java中的isHintBegin方法可知，Oracle的hint语法为`/*+ hint something*/`，一个是感叹号，一个是加号）；

- **注释与后面的空格**，参考sharding-jdbc源码可知注释语法有3种：`//`，`--`，`/*`，这三种注释的处理有所不同，`//`和`--`被认为是单行注释，sharding-jdbc会直接通过当前一整行；而`/*`被认为是多行注释，如果是`/*`，那么sharding-jdbc会直接跳到`*/`后面，例如MySQL的注释语法为`"  /*hello afei*/SELECT o.* FROM t_order where o.order_id=?"`这条SQL有注释`/*hello afei*/`；

#### 3.获取token

对应的核心实现源码如下（Lexer.java）：

```
public final void nextToken() {
    skipIgnoredToken();
    if (isVariableBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanVariable();
    } else if (isNCharBegin()) {
        currentToken = new Tokenizer(input, dictionary, ++offset).scanChars();
    } else if (isIdentifierBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanIdentifier();
    } else if (isHexDecimalBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanHexDecimal();
    } else if (isNumberBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanNumber();
    } else if (isSymbolBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanSymbol();
    } else if (isCharsBegin()) {
        currentToken = new Tokenizer(input, dictionary, offset).scanChars();
    } else if (isEnd()) {
        currentToken = new Token(Assist.END, "", offset);
    } else {
        currentToken = new Token(Assist.ERROR, "", offset);
    }
    offset = currentToken.getEndPosition();
}
```

由于接下来的SQL解析都会调用这个nextToken()方法，所以为了更好的分析SQL解析过程，接下来详细剖析它的逻辑，由其源码可知，其逻辑主要分为两个部分：

1. 调用skipIgnoredToken()跳过忽略的token（上面已经分析了哪些属于忽略的token）
2. 调用`is***Begin()`方法判断类型然后构造Token；`is***Begin()`主要有下面提到的这些：

| 条件                    | 说明                                                         |
| :---------------------- | :----------------------------------------------------------- |
| **isVariableBegin()**   | 是否变量开头，即@，例如这种SQL：**select @a from dual**（连续两个@符号即@@也是可以的），其中a是一个定义的变量；select @a from dual这条SQL解析到@的时候，得到的token为**{type:Literals.VARIABLE, literals:@a, endPostion:9}**（这个token的endPostion就是@a后面的位置） |
| **isNCharBegin()**      | SQLServer的特殊语法，其他数据库都不支持。例如INSERT INTO employees VALUES(N'29730', N'Philippe', N'Horsford', 20, 1)，申明字符串为nvarchar类型 |
| **isIdentifierBegin()** | 是否以标识符开头，即a~z，A~Z，`，_，$；例如这种SQL：**select user_id from t_user**，解析到select的时候，得到的token为**{type:DefaultKeyword.SELECT, literals:SELECT, endPostion:6}**；或者这种SQL：**select `user_id` from t_user**，解析到`user_id`的时候，得到的token为**{type:Literals.IDENTIFIER, literals:`user_id`, endPostion:16}**； |
| **isHexDecimalBegin()** | 是否16进程符号开头，即0x。例如这种SQL：**select 0x21 from dual**，解析0x21的时候，得到的token为**{type:Literals.HEX, literals:0x21, endPostion:11}** |
| **isNumberBegin()**     | 是否数字开头，即0~9，例如这种SQL：**select \* from t_user limit 1**，limit 1这个1就是数字，解析到limit后面的1的时候，得到的token为**{type:Literals.INT, literals:1, endPostion:28}** |
| **isSymbolBegin()**     | 是否特殊符号开头，例如这种SQL：**select user_id from t_user limit ?**，，解析到？的时候，得到的token为**{type:Symbol.QUESTION, literals:?, endPostion:34}** |
| **isCharsBegin()**      | 是否字符开头，即单引号或者双引号，例如这种SQL：**select 'afei' from t_user**，解析到 `afei`的时候，得到的token为**{type:Literals.CHARS, literals:afei, endPostion:13}** |
| **isEnd()**             | 是否SQL尾部，判断条件是**offset >= input.length()**，即遍历位置offset是否到了sql（input就是sql）尾部。得到的token为**{type:Assist.END, literals:"", endPostion:20}** |

> **说明**：nextToken()的逻辑非常重要，其调用贯穿在整个sharding-jdbc的词法分析过程中，根据这段分析逻辑就能得出文章前面SQL语句的词法分析结果了。

### SQL解析器

得到词法分析器后，就会根据它得到SQL解析器，对应的源码是：`SQLParserFactory.newInstance(dbType, lexerEngine.getCurrentToken().getType(), shardingRule, lexerEngine)`，**lexerEngine.getCurrentToken().getType()**就是上面解析得到的第一个token的类型，核心实现源码如下：

```
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLParserFactory {

    public static SQLParser newInstance(final DatabaseType dbType, final TokenType tokenType, final ShardingRule shardingRule, final LexerEngine lexerEngine) {
        // 第一个token类型一定是默认关键词，否则抛出异常。构造这种异常很简单，只需要定义的SQL以MySQLKeyword中的关键词开头即可，例如"show create table t_afei"
        if (!(tokenType instanceof DefaultKeyword)) {
            throw new SQLParsingUnsupportedException(tokenType);
        }
        // 根据第一个token类型得到具体解析器，且第一个token必须是：select，insert，update，delete，create，alter，drop，truncate，set，commit，rollback，savepoint和begin中的一个，否则会抛出SQLParsingUnsupportedException异常。
        switch ((DefaultKeyword) tokenType)  {
            // 从这里可知，为什么前面要调用lexerEngine.nextToken()获取第一个token，SQLParserFactory根据这个token才能确实哪种类型的SQL解析器
            case SELECT:
                return SelectParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case INSERT:
                return InsertParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case UPDATE:
                return UpdateParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case DELETE:
                return DeleteParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case CREATE:
                return CreateParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case ALTER:
                return AlterParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case DROP:
                return DropParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            case TRUNCATE:
                return TruncateParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            //sharding-jdbc2.x相比1.x多支持了set，commit，rollback，savepoint和begin。
            case SET:
            case COMMIT:
            case ROLLBACK:
            case SAVEPOINT:
            case BEGIN:
                return TCLParserFactory.newInstance(dbType, shardingRule, lexerEngine);
            default:
                throw new SQLParsingUnsupportedException(lexerEngine.getCurrentToken().getType());
        }
    }
}
```

从上面的代码可知，得到的SQL解析器的一些主要属性有：

1. 数据库类型（dbType）；
2. 分库分表规则（shardingRule）；
3. 词法分析器引擎（lexerEngine）；

> lexerEngine的唯一属性就是`Lexer lexer`，它的一些核心属性在上面有分析过：SQL语句input，包含SQL关键词的字典dictionary，SQL解析位置偏移量offset，当前解析得到的词令currentToken；

这篇文章分析了sharding-jdbc中SQL解析的准备工作，即词法分析，通过词法分析的结果就可以构造SQL解析器

## 4. sjdbc源码之delete解析

上一篇sharding-jdbc源码分析之词法分析已经分析到根据词法分析结果，以及sharding规则等信息，得到SQL解析器，得到SQL解析后，接下来就是进行SQL解析：

```
public final class SQLParsingEngine {   
    ... ... 
    public SQLStatement parse() {
        LexerEngine lexerEngine = LexerEngineFactory.newInstance(dbType, sql);
        lexerEngine.nextToken();
        SQLParser sqlParser = SQLParserFactory.newInstance(dbType, lexerEngine.getCurrentToken().getType(), shardingRule, lexerEngine);
        // 得到SQL解析器后，对SQL进行解析
        return sqlParser.parse();
    }
}
```

> 这段代码在路由之前调用，因为只有SQL解析后，才知道如何路由，调用处请参考ParsingSQLRouter.java的parse()方法。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJH51hxRC5v0IfJJyDpgkxFKygYeTInrOEk8FZUjezkRKdJ4yD8F6EXQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

# DELETE语法

准备分析delete这类SQL解析之前，首先看一下mysql官方对delete语法定义，因为SQL解析跟语法息息相关，mysql的delete语法分为两种：**单表**和**多表**。两种语法如下所示：

**Single-Table Syntax**：

```
DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM tbl_name
    [PARTITION (partition_name [, partition_name] ...)]
    [WHERE where_condition]
    [ORDER BY ...]
    [LIMIT row_count]
```

**Multiple-Table Syntax**：

```
DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
    tbl_name[.*] [, tbl_name[.*]] ...
    FROM table_references
    [WHERE where_condition]

DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
    FROM tbl_name[.*] [, tbl_name[.*]] ...
    USING table_references
    [WHERE where_condition]
```

> 摘自：https://dev.mysql.com/doc/refman/5.7/en/delete.html

# DELETE解析

接下来分析sharding-jdbc是如何解析delete类型的SQL语句的，通过调用`sqlParser.parse();`得到SQL解析器后，可知调用的是执行**AbstractDeleteParser**中**parse()**方法解析sql，核心源码如下：

```
@RequiredArgsConstructor
public abstract class AbstractDeleteParser implements SQLParser {

    @Override
    public DMLStatement parse() {
        lexerEngine.nextToken();
        lexerEngine.skipAll(getSkippedKeywordsBetweenDeleteAndTable());
        lexerEngine.unsupportedIfEqual(getUnsupportedKeywordsBetweenDeleteAndTable());
        DMLStatement result = new DMLStatement();
        deleteClauseParserFacade.getTableReferencesClauseParser().parse(result, true);
        lexerEngine.skipUntil(DefaultKeyword.WHERE);
        deleteClauseParserFacade.getWhereClauseParser().parse(shardingRule, result, Collections.<SelectItem>emptyList());
        return result;
    }
    ... ...
}
```

下面对delete类型sql的解析，以`delete ignore from t_order where user_id=? and order_id=?`为例进行分析。需要说明的是，sharding-jdbc2.x还不支持"多表删除语法"，否则会报错：**Cannot support Multiple-Table**。

### 1.取下一个token

由parse()源码可知，delete解析第1步就是调用 `lexerEngine.nextToken()`，nextToken()在上一篇文章《3. sjdbc源码之词法分析》已经分析过，即跳到下一个token，由于任意SQL解析都会在SQLParsingEngine中调用lexerEngine.nextToken()，这里再调用lexerEngine.nextToken()，所以总计已经跳过两个token，即跳到了**FROM**这个token。

**问题**：为什么要一开始就调用nextToken()呢？
回到delete的语法：`DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM... ...`，**DELETE FROM**一定会有， **LOW_PRIORITY**，**QUICK** 和**IGNORE**几个关键词可选，即在表名之前，至少有两个token（delete和from）。所以跳过两个token后再进行下一步操作（SQLParsingEngine.parse()中调用了一次nextToken()，这里再调用一次nextToken()）；

### 2. 跳过表名前面的关键词

由parse()源码可知，delete解析第2步就是调用`lexerEngine.skipAll(getSkippedKeywordsBetweenDeleteAndTable());`。因为如果sql中delete和from之间有一些关键词的话（mysql数据库需要跳过的关键词有**LOW_PRIORITY**,**QUICK**, **IGNORE**, **FROM**，参考**MySQLDeleteParser.java**），这时候还没跳到表名位置，执行这一步代码，就是为了强行跳到表名即`t_order`这个token，这时候currentToken就是`Token(type=IDENTIFIER, literals=t_order, endPosition=26)`。

### 3. 不支持关键词检查

由parse()源码可知，delete解析第3步就是调用`lexerEngine.unsupportedIfEqual(getUnsupportedKeywordsBetweenDeleteAndTable());`，即**检查表名后不支持的关键词**，除了SQLServer数据库的delete不支持top和output以外，其他都没有限制。

### 4. 解析表名

由parse()源码可知，delete解析第4步就是调用`deleteClauseParserFacade.getTableReferencesClauseParser().parse(result, true);`，即**解析sql中的表名**，并把表名（如果有的话）封装到**DMLStatement**的**tables**和**sqlTokens**属性中：

```
// 有表名才有后面的属性封装，否则直接返回。
if (Strings.isNullOrEmpty(tableName)) {
    return;
}
Optional<String> alias = aliasExpressionParser.parseTableAlias();
// delete语句解析表名时，isSingleTableOnly参数值固定为true。因为sharding-jdbc Cannot support Multiple-Table
if (isSingleTableOnly || shardingRule.tryFindTableRule(tableName).isPresent()
        || shardingRule.findBindingTableRule(tableName).isPresent()
        || shardingRule.getDataSourceMap().containsKey(shardingRule.getDefaultDataSourceName())) {
    // 封装sqlTokens和table两个属性。
    sqlStatement.getSqlTokens().add(new TableToken(beginPosition, literals));
    sqlStatement.getTables().add(new Table(tableName, alias));
}
```

解析表名过程中还会有一些不支持语法检查，例如：**Cannot support SQL for schema.table**，通过SQL（**delete ignore from afei_test.t_order where user_id=? and order_id=?**）可以得到这种异常；

### 5.跳到where

由parse()源码可知，delete解析第5步就是调用`lexerEngine.skipUntil(DefaultKeyword.WHERE);`，即强行跳到where这个token位置；

### 6. 解析条件

由parse()源码可知，insert解析第6步就是调用`deleteClauseParserFacade.getWhereClauseParser().parse(shardingRule, result, Collections.<SelectItem>emptyList());`，解析delete语句的where条件，并封装到**DMLStatement**的**conditions**属性中：

```
do {
    parseComparisonCondition(shardingRule, sqlStatement, items);
} while (lexerEngine.skipIfEqual(DefaultKeyword.AND));
lexerEngine.unsupportedIfEqual(DefaultKeyword.OR);
```

由这段源码可知，sharding-jdbc2.x对delete语句目前只支持and，并不支持or，例如这种SQL：`delete ignore from t_user where user_id=? or order_id=?`就会抛出异常：

```java
io.shardingjdbc.core.parsing.parser.exception.SQLParsingUnsupportedException: Not supported token 'OR'.

    at io.shardingjdbc.core.parsing.lexer.LexerEngine.unsupportedIfEqual(LexerEngine.java:178)
    at io.shardingjdbc.core.parsing.parser.clause.WhereClauseParser.parseConditions(WhereClauseParser.java:76)
    at io.shardingjdbc.core.parsing.parser.clause.WhereClauseParser.parse(WhereClauseParser.java:68)
```

> 说明：sharding-jdbc3.x已经支持or这种条件了。

parse()方法运行完得到的结果就是DMLStatement，即delete类型的SQL解析结果。接下来就需要这个SQL解析结果才能进行SQL路由（ParsingSQLRouter.route()方法）：

```java
// 根据sql解析结果和参数的值，得到路由结果
RoutingResult routingResult = route(parameters, sqlStatement);
```

## 6. sjdbc源码之路由

继续以`sharding-jdbc-core`模块中的`ShardingPreparedStatementTest.java`为基础，剖析分库分表核心功能`--`**路由**；

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJDibhldgKOjbSxcxFHSS0S7rf2HXYSVoVoTtq1tM9s88nHZXWQj0PkYA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

ShardingPreparedStatementTest.java中查询调用`preparedStatement.executeQuery()`，即调用**ShardingPreparedStatement**中的`executeQuery()`方法，核心源码如下：

```
@Override
public ResultSet executeQuery() throws SQLException {
    ResultSet result;
    try {
        // 核心方法route()，即解析SQL如何路由执行
        Collection<PreparedStatementUnit> preparedStatementUnits = route();
        // 根据路由信息执行SQL
        List<ResultSet> resultSets = new PreparedStatementExecutor(
                getConnection().getShardingContext().getExecutorEngine(), routeResult.getSqlStatement().getType(), preparedStatementUnits, getParameters()).executeQuery();
        // 对返回的结果进行merge合并
        result = new ShardingResultSet(resultSets, new MergeEngine(resultSets, (SelectStatement) routeResult.getSqlStatement()).merge());
    } finally {
        clearBatch();
    }
    currentResultSet = result;
    return result;
}
```

> 通过上面的源码可知，SQL查询几个核心：路由`route()`，执行`execute()`和结果合并`merge()`，这篇文章主要分析路由的实现；

### 路由选择

接下来分析下面这段代码是如何取得路由信息的：

```
Collection<PreparedStatementUnit> preparedStatementUnits = route();
```

说明：SQLRouter接口有两个实现类：**DatabaseHintSQLRouter**和**ParsingSQLRouter**，两者之间如何选择，源码如下：

```
public final class SQLRouterFactory {
    public static SQLRouter createSQLRouter(final ShardingContext shardingContext) {
        // 所以如果要选择DatabaseHintSQLRouter，那么需要hint语法
        return HintManagerHolder.isDatabaseShardingOnly() ? new DatabaseHintSQLRouter(shardingContext) : new ParsingSQLRouter(shardingContext);
    }
}
```

- DatabaseHintSQLRouter

所以，选择DatabaseHintSQLRouter的用法如下：

```
HintManager hintManager = HintManager.getInstance();
// 数据库sharding值强制设置为10，表sharding值强制设置为1001。这样的话，只会路由到dataSource_jdbc_0数据源的表t_order_1中
hintManager.addDatabaseShardingValue("t_order", "user_id", 10);
hintManager.addTableShardingValue("t_order", "order_id", 1001);
// 这里就是执行sql的地方，如果是mybatis框架
orderMapper.selectByOrderId(orderId);
```

所以，Hint语法还是比较简单的。由于我们的测试用例没有使用Hint语法强制路由数据库&表，所以调用ParsingSQLRouter中的route()方法；

#### ParsingSQLRouter

ParsingSQLRouter路由核心源码如下：

```
private RoutingResult route(final List<Object> parameters, final SQLStatement sqlStatement) {
    Collection<String> tableNames = sqlStatement.getTables().getTableNames();
    RoutingEngine routingEngine;
    // DDL类型SQL由特殊的路由引擎处理
    if (sqlStatement instanceof DDLStatement) {
        routingEngine = new DDLRoutingEngine(shardingRule, parameters, (DDLStatement) sqlStatement); 
    } else if (tableNames.isEmpty()) {
        // 如果没有表名，需要路由到所有数据库，例如"select 1"
        routingEngine = new DatabaseAllRoutingEngine(shardingRule.getDataSourceMap());
    } else if (1 == tableNames.size() || shardingRule.isAllBindingTables(tableNames) || shardingRule.isAllInDefaultDataSource(tableNames)) {
        // 如果sql中只有一张表，或者多个表名之间是绑定表关系，或者所有表都在默认数据源指定的数据库中（即不参与分库分表的表），那么用SimpleRoutingEngine作为路由判断引擎；
        routingEngine = new SimpleRoutingEngine(shardingRule, parameters, tableNames.iterator().next(), sqlStatement);
    } else {
        // 复杂路由
        routingEngine = new ComplexRoutingEngine(shardingRule, parameters, tableNames, sqlStatement);
    }
    return routingEngine.route();
}
```

### 简单路由or复杂路由

由上面这段代码可知，满足如下任意一个条件就走简单路由，否则为复杂路由，假设sharding规则如下：

```
final ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
shardingRuleConfig.getTableRuleConfigs().add(orderItemTableRuleConfig);
shardingRuleConfig.getTableRuleConfigs().add(configTableRuleConfig);
shardingRuleConfig.getBindingTableGroups().add("t_order_item, t_order");
ShardingRule shardingRule = shardingRuleConfig.build(entry.getValue());
```

- **是否只有一张表**

说明：这个"一张表"并不是指SQL中只有一张表，而是**有分库分表规则的表数量**，例如AbstractShardingJDBCDatabaseAndTableTest中这段构造ShardingRule的源码，总计有三张表有配置规则：t_order，t_order_item，t_config。所以如果有这样的SQL：`SELECT tod.order_id, tod.user_id FROM t_order tod join t_status tc on tod.status=tc.status WHERE tod.status = ?`，只有t_order涉及sharding规则，t_status并不涉及，所以任然认为"只有一张表"；

- **是否都是绑定表**

说明：isAllBindingTables(tableNames)判断tableNames是否都属于绑定表，根据刚才那段构造ShardingRule的源码可知：`shardingRuleConfig.getBindingTableGroups().add("t_order_item, t_order");`，`t_order`和`t_order_item`互为绑定表，那么：`SELECT od.user_id, od.order_id, oi.item_id, od.status FROM t_order od join t_order_item oi on od.order_id=oi.order_id`这个SQL只有`t_order`和`t_order_item`两个表且互为绑定表，那么shardingRule.isAllBindingTables(tableNames)为true；

- **是否属于默认数据源**

说明：如果SQL中涉及的表全部属于默认数据源中，那么无论多少张表，都认为isAllInDefaultDataSource()为true，即走简单路由。

表是否属于默认数据源的判断依据：不在`Collection<TableRule>`中，如果是spring配置方式，则不在`<sharding:table-rules>`中。例如如下配置，`t_order`和`t_order_item`都有表规则，如果SQL中有这两张表的任何一张表，都认为不属于默认数据源：

```
<sharding:data-source id="shardingDatasource">
    <sharding:sharding-rule data-source-names="dbtbl_0,dbtbl_1" default-data-source-name="dbtbl_0">
        <sharding:table-rules>
            <sharding:table-rule logic-table="t_order" .../>
            <sharding:table-rule logic-table="t_order_item" .../>
        </sharding:table-rules>
        <sharding:binding-table-rules>
            <sharding:binding-table-rule logic-tables="t_order, t_order_item" />
        </sharding:binding-table-rules>
    </sharding:sharding-rule>
</sharding:data-source>
```

### 简单路由

执行SQL：`"SELECT o.* FROM t_order o where o.user_id=? AND o.order_id=?"`时，由于SQL中只有一个表（1 == tableNames.size()），所以路由引擎是**SimpleRoutingEngine**；`SimpleRoutingEngine.route()`核心源码如下：

```
@Override
public RoutingResult route() {
    // 根据逻辑表得到tableRule，逻辑表为t_order；表规则的配置为：orderActualDataNodes.add(dataSourceName + ".t_order_${0..1}")，所以有两个实际表即t_order_0和t_order_1；
    TableRule tableRule = shardingRule.getTableRule(logicTableName);
    // 得到分库分表规则的数据库sharding列，即user_id（ShardingValue对象包含的值有{logicTableName:t_order,columnName:user_id,value:10}）
    List<ShardingValue> databaseShardingValues = getDatabaseShardingValues(tableRule);
    // 得到分库分表规则的表sharding列，即order_id
    List<ShardingValue> tableShardingValues = getTableShardingValues(tableRule);
    // 根据规则先路由数据源：即根据user_id的值取模路由
    Collection<String> routedDataSources = routeDataSources(tableRule, databaseShardingValues);
    Collection<DataNode> routedDataNodes = new LinkedList<>();
    // 遍历路由的数据源集合（如果user_id为偶数，那么路由到dataSource_jdbc_0数据源；否则路由到dataSource_jdbc_1数据源）
    for (String each : routedDataSources) {
        // 每个数据源下，表路由结果
        routedDataNodes.addAll(routeTables(tableRule, each, tableShardingValues));
    }
    // 数据源路由结果*表路由结果=数据节点路由结果（routedDataNodes），然后将节点路由结果封装成路由结果
    return generateRoutingResult(routedDataNodes);    
}
```

- **数据源路由详细解读**

由于数据源的sharding策略为：

```
shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(
new StandardShardingStrategyConfiguration(
"user_id", PreciseOrderShardingAlgorithm.class.getName(), RangeOrderShardingAlgorithm.class.getName())
);          
```

where条件为`where o.user_id=? AND o.order_id=?`，即where条件中有`user_id`，根据取模路由策略，当`user_id`为奇数时，数据源为`dataSource_jdbc_1`；当`user_id`为偶数时，数据源为`dataSource_jdbc_0`；

- **表路由详细解读**

表的sharding策略为：

```
shardingRuleConfig.setDefaultTableShardingStrategyConfig(
                    new StandardShardingStrategyConfiguration(
                    "order_id", PreciseOrderShardingAlgorithm.class.getName(), RangeOrderShardingAlgorithm.class.getName()));
```

where条件中有`order_id`，根据取模路由策略，当`order_id`为奇数时，表为`t_order_1`；当`order_id`为偶数时，表为`t_order_0`；

> 结论：最终需要执行SQL的数据节点（DataNode，由数据源名称和数据表组成）的总个数为：**路由到的数据源个数\*路由到的实际表个数**；

- **示例**

下面给出几个路由示例，以便更好的理解分库分表路由规则：
**示例1**：**where o.order_id=1001 AND o.user_id=10**，user_id=10所以路由得到数据源为dataSource_jdbc_0; order_id=1001，路由得到实际表为t_order_1；那么最终只需在`dataSource_jdbc_0.t_order_1`数据节点上执行即可

------

**示例2**：**where o.order_id=1000**，user_id没有值所以路由得到所有数据源dataSource_jdbc_0和dataSource_jdbc_1; order_id=1000，路由得到实际表为t_order_0；那么最终需在`dataSource_jdbc_0.t_order_0`和`dataSource_jdbc_1.t_order_0`两个数据节点执行；

------

**示例3**：**where o.user_id=11**，user_id=11所以路由得到数据源为dataSource_jdbc_1; order_id没有值所以路由得到实际表为t_order_0和t_order_1；那么最终需要在`dataSource_jdbc_1.t_order_0`和`dataSource_jdbc_1.t_order_1`两个数据节点执行即可；

### 复杂路由

首先构造这种复杂路由场景`--`t_order和t_order_item分库分表且绑定表关系，加入一个新的分库分表t_config，执行的SQL为：

```
SELECT od.user_id, od.order_id, od.status FROM `t_config` tu 
join t_order od on tu.status=od.status 
join t_order_item oi on od.order_id=oi.order_id 
where tu.`status`='init' and oi.user_id=? and oi.order_id=?
```

> 构造的这个SQL会走复杂路由的逻辑（不是DDL，有三张表，t_config和t_order/t_order_item不是绑定表关系，t_order/t_order_item有分库分表规则所以不属于默认数据源）；

复杂路由引擎的核心逻辑就是拆分成多个简单路由，然后求笛卡尔积，复杂路由核心源码如下：

```
@RequiredArgsConstructor
@Slf4j
public final class ComplexRoutingEngine implements RoutingEngine {

    // 分库分表规则
    private final ShardingRule shardingRule;

    // SQL请求参数，猪油一个user_id的值为10
    private final List<Object> parameters;

    // 逻辑表集合：t_order，t_order_item，t_user，三个逻辑表
    private final Collection<String> logicTables;

    // SQL解析结果
    private final SQLStatement sqlStatement;

    // 复杂路由的核心逻辑
    @Override
    public RoutingResult route() {
        Collection<RoutingResult> result = new ArrayList<>(logicTables.size());
        Collection<String> bindingTableNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        // 遍历逻辑表集合
        for (String each : logicTables) {
            Optional<TableRule> tableRule = shardingRule.tryFindTableRule(each);
            // 如果遍历的表配置了分库分表规则才需要处理
            if (tableRule.isPresent()) {
                // 如果绑定关系表已经处理过，那么不需要再处理，例如t_order处理过，由于t_order_item与其是绑定关系，那么不需要再处理
                if (!bindingTableNames.contains(each)) {
                    // 根据当前遍历的逻辑表构造一个简单路由规则
                    result.add(new SimpleRoutingEngine(shardingRule, parameters, tableRule.get().getLogicTable(), sqlStatement).route());
                }

                // 根据当前逻辑表，查找其对应的所有绑定表，例如根据t_order就能够查询出t_order和t_order_item；其目的是N个绑定表只需要路由一个绑定表即可，因为绑定表之间的路由关系完全一致。
                Optional<BindingTableRule> bindingTableRule = shardingRule.findBindingTableRule(each);
                if (bindingTableRule.isPresent()) {
                    bindingTableNames.addAll(Lists.transform(bindingTableRule.get().getTableRules(), new Function<TableRule, String>() {

                        @Override
                        public String apply(final TableRule input) {
                            return input.getLogicTable();
                        }
                    }));
                }
            }
        }
        log.trace("mixed tables sharding result: {}", result);
        // 如果是复杂路由，但是路由结果为空，那么抛出异常
        if (result.isEmpty()) {
            throw new ShardingJdbcException("Cannot find table rule and default data source with logic tables: '%s'", logicTables);
        }
        // 如果结果的size为1，那么直接返回即可
        if (1 == result.size()) {
            return result.iterator().next();
        }
        // 对刚刚的路由结果集合计算笛卡尔积，就是最终复杂的路由结果
        return new CartesianRoutingEngine(result).route();
    }
}
```

由上面源码分析可知，会分别对t_config和t_order构造简单路由（t_order_item和t_order是绑定关系，二者取其一即可）；

- t_config需要分库不需要分表（因为不涉及分表），所以t_config这个逻辑表的简单路由结果为：dataSource_jdbc_0.t_config和dataSource_jdbc_1.t_config，有2个数据节点；
- t_order_item分库分表，且有请求参数user_id=10和order_id=1001，所以t_order_item的简单路由结果为：dataSource_jdbc_0.t_order_item_1，只有1个数据节点（user_id=10路由数据源dataSource_jdbc_0，order_id=1001路由表t_order_item_1）。

#### CartesianRoutingEngine

如上分析，求得简单路由结果集后，求笛卡尔积就是复杂路由的最终路由结果，笛卡尔积路由引擎CartesianRoutingEngine的核心源码如下：

```
@RequiredArgsConstructor
@Slf4j
public final class CartesianRoutingEngine implements RoutingEngine {

    private final Collection<RoutingResult> routingResults;

    @Override
    public CartesianRoutingResult route() {
        CartesianRoutingResult result = new CartesianRoutingResult();
        // getDataSourceLogicTablesMap()的分析参考下面的分析
        for (Entry<String, Set<String>> entry : getDataSourceLogicTablesMap().entrySet()) {
            // 根据数据源&逻辑表，得到实际表集合，即[["t_config"],["t_order_0","t_order_1"]]
            List<Set<String>> actualTableGroups = getActualTableGroups(entry.getKey(), entry.getValue());
            // 把逻辑表名封装，TableUnit的属性有：数据源名称，逻辑表名，实际表名（这三个属性才能确定最终访问的表）
            List<Set<TableUnit>> tableUnitGroups = toTableUnitGroups(entry.getKey(), actualTableGroups);
            // 计算所有实际表的笛卡尔积
            result.merge(entry.getKey(), getCartesianTableReferences(Sets.cartesianProduct(tableUnitGroups)));
        }
        log.trace("cartesian tables sharding result: {}", result);
        return result;
    }

    // 得到数据源-逻辑表集合组成的Map
    private Map<String, Set<String>> getDataSourceLogicTablesMap() {
        // 这里很关键，是得到数据源的交集（上面分析时t_config逻辑表路由到数据源dataSource_jdbc_0和dataSource_jdbc_1，而t_order_item表路由到数据源dataSource_jdbc_0，最终数据源交集就是dataSource_jdbc_0）
        Collection<String> intersectionDataSources = getIntersectionDataSources();
        Map<String, Set<String>> result = new HashMap<>(routingResults.size());
        for (RoutingResult each : routingResults) {
            for (Entry<String, Set<String>> entry : each.getTableUnits().getDataSourceLogicTablesMap(intersectionDataSources).entrySet()) {
                if (result.containsKey(entry.getKey())) {
                    result.get(entry.getKey()).addAll(entry.getValue());
                } else {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        // 得到的最终结果为数据源-逻辑表集合组成的Map，这里就是{"dataSource_jdbc_0":["t_order_item", "t_config"]}
        return result;
    }
    ... ...
}
```

`sql.show=true`时输出的结果如下，可以看到重写后的1条实际SQL：`t_config和t_order_item_1`（t_order_item与t_order是绑定表，保持一致）：

```
[INFO ] 09:19:45.219 [main] Sharding-JDBC-SQL - Logic SQL: SELECT od.user_id, od.order_id, od.status FROM `t_config` tu join t_order od on tu.status=od.status join t_order_item oi on od.order_id=oi.order_id where tu.`status`='init' and oi.user_id=? and oi.order_id=? 
... ...
[INFO ] 09:19:45.224 [main] Sharding-JDBC-SQL - Actual SQL: dataSource_jdbc_0 ::: SELECT od.user_id, od.order_id, od.status FROM t_config tu join t_order_1 od on tu.status=od.status join t_order_item_1 oi on od.order_id=oi.order_id where tu.`status`='init' and oi.user_id=? and oi.order_id=? ::: [10, 1001]
```

如果把SQL条件做如下调整：

```
where tu.`status`='init' and oi.user_id=? and oi.order_id=?
调整为
where tu.`status`='init' and oi.order_id=?
```

这样的话：

- t_config需要分库不需要分表（因为不涉及分表），所以t_config这个逻辑表的简单路由结果为：dataSource_jdbc_0.t_config和dataSource_jdbc_1.t_config，有2个数据节点；
- t_order_item分库分表，但是只有请求参数order_id=1001，所以t_order_item的简单路由结果为：dataSource_jdbc_0.t_order_item_1和dataSource_jdbc_1.t_order_item_1，也有2个数据节点。

那么最终的路由结果如下，两个数据源dataSource_jdbc_0 和dataSource_jdbc_1 都会路由到：

```verilog
[INFO ] 10:09:59.076 [main] Sharding-JDBC-SQL - Actual SQL: dataSource_jdbc_1 ::: SELECT od.user_id, od.order_id, od.status FROM t_config tu 
join t_order_1 od on tu.status=od.status 
join t_order_item_1 oi on od.order_id=oi.order_id 
where tu.`status`='init' and oi.order_id=? ::: [1001]
[INFO ] 10:09:59.077 [main] Sharding-JDBC-SQL - Actual SQL: dataSource_jdbc_0 ::: SELECT od.user_id, od.order_id, od.status FROM t_config tu 
join t_order_1 od on tu.status=od.status 
join t_order_item_1 oi on od.order_id=oi.order_id 
where tu.`status`='init' and oi.order_id=? ::: [1001]
```

## 7. sjdbc源码之重写

经历过前面的词法分析，SQL解析，路由后，接下来就是rewrite即重写了。
核心源码就在`sharding-jdbc-core`模块的`io.shardingjdbc.core.rewrite`目录下，包含两个Java文件：

- **SQLBuilder.java**--重写的最后一步就是调用这个类中的toSQL()方法，作用是根据N个部分逻辑SQL和Token得到重写的最终结果即实际SQL；
- **SQLRewriteEngine.java**--重写引擎，sharding-jdbc重写SQL最核心的地方，包括对表名，limit offset, rowCount，GROUP BY等的重写。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJ2Ixh9MkLbGyPCsxtPAn1R192SOayQKicTF9BXXavGTnzEUx5mPdwLug/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

还是以ShardingPreparedStatementTest.java做为debug入口，对SQL重写进行分析，前面已经分析到ParsingSQLRouter.java的route方法中，由这个方法的源码可知，路由后就是SQL重写：

```
@Override
public SQLRouteResult route(final String logicSQL, final List<Object> parameters, final SQLStatement sqlStatement) {
    RoutingResult routingResult = route(parameters, sqlStatement);
    // 根据sharding规则，以及逻辑sql等信息构造SQL重写引擎
    SQLRewriteEngine rewriteEngine = new SQLRewriteEngine(shardingRule, logicSQL, databaseType, sqlStatement);
    boolean isSingleRouting = routingResult.isSingleRouting();
    if (sqlStatement instanceof SelectStatement && null != ((SelectStatement) sqlStatement).getLimit()) {
        // 判断是否需要重写limit
        processLimit(parameters, (SelectStatement) sqlStatement, isSingleRouting);
    }
    SQLBuilder sqlBuilder = rewriteEngine.rewrite(!isSingleRouting);
    // 这里省略了路由结果是CartesianRoutingResult的重写过程, 原理都一样
    for (TableUnit each : routingResult.getTableUnits().getTableUnits()) {
        result.getExecutionUnits().add(new SQLExecutionUnit(each.getDataSourceName(), rewriteEngine.generateSQL(each, sqlBuilder)));
    }
    // 这是sharding-jdbc一个非常重要的配置, 如果sql.show为true，就会输出SQL重写前的逻辑sql，以及重写后的实际sql
    if (showSQL) {
        SQLLogger.logSQL(logicSQL, sqlStatement, result.getExecutionUnits(), parameters);
    }
    return result;
}
```

------

- **processLimit**

如果是SQL类型语句，并且带有limit，那么执行processLimit()：

1. 首先判断是否是single路由，即只路由到一个数据源的一张表中。如果是，则不需要重写limit，例如SQL：`select order_id, user_id FROM t_order t1 WHERE t1.user_id=10 and t1.order_id=1001 and t1.status = ? limit 1,2`。
2. 如果需要重写limit的话，先判断是否要抓取所有数据，条件是带有group by条件。如果是，那么limit n要重写为`limit INTEGER.MAX_VALUE`。否则只需要将limit m,n重写为limit m+n即可。

------

- **rewriteEngine.rewrite(!isSingleRouting)**

重写引擎对sql进行重写。示例SQL：`select order_id, user_id FROM t_order t1 WHERE t1.user_id=10 and t1.status = ? order by order_id asc limit 1,2`，这条SQL有三个sqlToken：TableToken（t_order），OffsetToken（1），rowCountToken（2）：

1. 如果前面的词法分析&SQL解析结果没有任何Token，那么直接返回原生SQL，即不需要重写。例如SQL：`select * FROM t_status t1 WHERE t1.status = ?`，前提是t_status表没有sharding规则；
2. 如果有sqlToken的话，根据解析结果得到的Token在SQL中出现的位置正序排列。
3. 遍历这些Token；
4. 如果当前正在遍历第一个Token，把这个Token之前的SQL部分截取出来，即截取示例SQL中的：`select order_id, user_id FROM`；
5. 对当前遍历的Token进行判断：

- 如果是TableToken，截取TableToken到下一个Token这中间部分的sql，即：`t1 WHERE t1.user_id=10 and t1.status = ? order by order_id asc limit`；
- 如果是OffsetToken则需要分两种情况：如果不需要重写，那么维持原生SQL即可；如果需要重写，那么limit m,n中的m需要重写为0。最后再截取OffsetToken到下一个Token这中间部分的sql。
- 如果是RowCountToken，则需要几个情况：如果不需要重写，那么维持不变；如果需要重写且有group by这样的条件，那么limit m,n中的n需要重写Integer.MAX_VALUE；如果需要重写，则limit,n中的n需要重写为m+n。最后再截取OffsetToken到下一个Token这中间部分的sql。
- 如果SQL中有GROUP BY columName，需要重写为GROUP BY columnName ORDER BY columName ASC（因为如果SQL中有GROUP BY，那么结果合并时需要每个路由表中的结果先排序，再聚合）；

   \6. 如果还有Token，跳到第4步，直到Token遍历完成。

示例SQL调用rewrite方法后的结果为：

![after rewrite](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHn2F6QAqQ9vHpHPfEfW6zj3O2MGvCmmNMBPm4nWWPjCLZ0wIBCoVBBv1YAntnntKMOaNibLYNkx3Pg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)after rewrite

核心源码如下：

```
// 参数isRewriteLimit表示limit是否需要重写
public SQLBuilder rewrite(final boolean isRewriteLimit) {
    SQLBuilder result = new SQLBuilder();
    if (sqlTokens.isEmpty()) {
        // 如果没有任何sqlToken，那么直接返回原生SQL，这种情况逻辑SQL和实际SQL完全等价
        result.appendLiterals(originalSQL);
        return result;
    }
    // 遍历的sqlToken统计，即当前遍历了多少个Token了；
    int count = 0;
    // 根据Token的beginPosition即出现的位置排序且正序排列
    sortByBeginPosition();
    for (SQLToken each : sqlTokens) {
        if (0 == count) {
            // 如果是第一个sqlToken：截取从原生SQL的开始位置到第一个token起始位置之间的内容，例如"SELECT x.id FROM table_x x LIMIT 2, 2"这条SQL的第一个token是TableToken，即table_x所在位置，所以截取内容为"SELECT x.id FROM "
            result.appendLiterals(originalSQL.substring(0, each.getBeginPosition()));
        }
        if (each instanceof TableToken) {
            // 看后面的"表名重写分析"
            appendTableToken(result, (TableToken) each, count, sqlTokens);
        } else if (each instanceof ItemsToken) {
            // ItemsToken是指当逻辑SQL有order by，group by这样的特殊条件时，需要在select的结果列中增加一些结果列，例如执行逻辑SQL:"SELECT o.* FROM t_order o where o.user_id=? order by o.order_id desc limit 2,3"，那么还需要增加结果列o.order_id AS ORDER_BY_DERIVED_0  
            appendItemsToken(result, (ItemsToken) each, count, sqlTokens);
        } else if (each instanceof RowCountToken) {
            // 看后面的"rowCount重写分析"
            appendLimitRowCount(result, (RowCountToken) each, count, sqlTokens, isRewriteLimit);
        } else if (each instanceof OffsetToken) {
            // 看后面的"offset重写分析"
            appendLimitOffsetToken(result, (OffsetToken) each, count, sqlTokens, isRewriteLimit);
        } else if (each instanceof OrderByToken) {
            appendOrderByToken(result, count, sqlTokens);
        }
        count++;
    }
    return result;
}
```

说明：遍历每个Token，即调用appendTableToken，appendItemsToken，appendLimitRowCount，appendLimitOffsetToken，appendOrderByToken时，都会在最后调用appendRest()，即追加余下部分内容，这个余下部分内容是指从当前遍历的token到下一个token之间的内容，例如SQL为`SELECT x.id FROM table_x x LIMIT 5, 10`，当遍历到`table_x`，即处理完TableToken后，由于下一个token为OffsetToken，即5，所以appendRest就是append这一段内容：" x LIMIT "--从table_x到5之间的内容；

- **rewriteEngine.generateSQL()**

经过前面的重写，SQL语句主要分为两部分：部分SQL+TableToken；例如示例SQL就被重写为下面三个部分：

```
Part1: select order_id, user_id FROM 
Part2: TableToken(tableName:"t_order") 
Part3: t1 WHERE t1.order_id=? and t1.status = ? asc limit 1,2
```

最后一步就是遍历所有路由结果，对每个路由（例如：数据源x中的实际表y），根据重写后的部分SQL，以及TableToken对应的实际表名，组装得到实际SQL。

而实际SQL和数据源组成最终的执行单元，即SQLExecutionUnit。N个路由结果TableUnit，就会有N个SQLExecutionUnit，即最终的路由结果SQLRouteResult。

------

- 测试用例角度分析

SQLRewriteEngineTest.java中任意一个测试用例可以大概看出重写的结果，例如：

```java
@Before
public void setUp() throws SQLException {
    // 测试用例中mock的sharding规则，table_x和table_y是绑定表关系
    shardingRule = new ShardingRuleMockBuilder().addGenerateKeyColumn("table_x", "id").addBindingTable("table_y").build();
    selectStatement = new SelectStatement();
    tableTokens = new HashMap<>(1, 1);
    // 这个map关系表示表名table_x需要重写为table_1
    tableTokens.put("table_x", "table_1");
}

@Test
public void assertRewriteForLimit() {
    selectStatement.setLimit(new Limit(DatabaseType.MySQL));
    // offset的值就是limit offset,rowCount中offset的值
    selectStatement.getLimit().setOffset(new LimitValue(2, -1, true));
    // rowCount的值就是limit offset,rowCount中rowCount的值
    selectStatement.getLimit().setRowCount(new LimitValue(2, -1, false));
    // TableToken的值表示表名table_x在原始SQL语句的偏移量是17的位置
    selectStatement.getSqlTokens().add(new TableToken(17, "table_x"));
    // OffsetToken的值表示offset在原始SQL语句的偏移量是33的位置（2就是offset的值）
    selectStatement.getSqlTokens().add(new OffsetToken(33, 2));
    // RowCountToken的值表示rowCount在原始SQL语句的偏移量是36的位置（2就是rowCount的值）
    selectStatement.getSqlTokens().add(new RowCountToken(36, 2));
    // selectStatement值模拟过程，实际上是SQL解释过程（SQL解释会单独分析）
    SQLRewriteEngine rewriteEngine = new SQLRewriteEngine(shardingRule, "SELECT x.id FROM table_x x LIMIT 2, 2", selectStatement);
    // 重写的核心就是这里了：rewriteEngine.rewrite(true)
    // 由重写前后可知，主要重写了表名，offset和rowCount，至于表名需要被重写为table_1的原因在setUp()方法中。
    assertThat(rewriteEngine.rewrite(true).toSQL(tableTokens), is("SELECT x.id FROM table_1 x LIMIT 0, 4"));
}
```

## 8. sjdbc源码之执行

路由完成后就决定了SQL需要在哪些数据源的哪些实际表中执行，接下来以执行SQL：`select order_id, user_id FROM t_order t1 WHERE t1.user_id=? and t1.status = ? order by order_id asc limit 1,2`为例，分析sharding-jdbc是如何执行重写后的SQL语句。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJPAOhJE2Q08ungYpZEuZ0TDJ5x1K8wWDiaGoPAZo0ZUMgRobtDUoxczw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

### 生成PreparedStatementUnit

根据前面的路由分析知，示例SQL的路由结果如下：

```
{dataSrouce:"dataSource_jdbc_0", sql:"select order_id, user_id FROM t_order_0 t1 WHERE t1.user_id=10 and t1.status = ? order by order_id asc limit 0,3"}
{dataSrouce:"dataSource_jdbc_0", sql:"select order_id, user_id FROM t_order_1 t1 WHERE t1.user_id=10 and t1.status = ? order by order_id asc limit 0,3"}
```

因为user_id是分库键且值为10，所以路由到dataSource_jdbc_0这个数据源；order_id是分表键但是where条件没有这个字段，所以路由的实际表为[t_order_0, t_order_1]；

得到路由结果后，在执行前，首先需要根据重写后的实际SQL生成PreparedStatementUnit：

```
public final class PreparedStatementUnit implements BaseStatementUnit {
    // SQLExecutionUnit包含两个重要信息：数据源名称dataSource和sql。
    private final SQLExecutionUnit sqlExecutionUnit;
    // PreparedStatement就是java.sql原生的类，调用Connection.prepareStatement(sql)就能得到
    private final PreparedStatement statement;
}
```

### 执行SQL

核心源码在ShardingPreparedStatement.java中，示例SQL是查询类型，所以核心源码如下（更新类型SQL可以查看executeUpdate()，原理一样）：

```
@Override
public ResultSet executeQuery() throws SQLException {
    ResultSet result;
    try {
        // 得到路由结果
        Collection<PreparedStatementUnit> preparedStatementUnits = route();
        // 先构造PreparedStatementExecutor，然后调用executeQuery()方法
        List<ResultSet> resultSets = new PreparedStatementExecutor(
                getConnection().getShardingContext().getExecutorEngine(), routeResult.getSqlStatement().getType(), preparedStatementUnits, getParameters()).executeQuery();
        // 结果归并，下一章节再分析
        result = new ShardingResultSet(resultSets, new MergeEngine(resultSets, (SelectStatement) routeResult.getSqlStatement()).merge(), this);
    } finally {
        clearBatch();
    }
    currentResultSet = result;
    return result;
}
```

PreparedStatementExecutor.executeQuery()会调用ExecutorEngine.executePreparedStatement()，所以执行的核心代码在**ExecutorEngine.java**中，核心源码如下：

```
public <T> List<T> executePreparedStatement(...) {
    // preparedStatementUnits就是前面分析的路由结果：有数据源名称和需要执行的实际SQL
    return execute(sqlType, preparedStatementUnits, Collections.singletonList(parameters), executeCallback);
}

// 所有SQL，不论是query类型，还是update类型，最终都调用到这里
private  <T> List<T> execute(...) {
    // 封装一个本次逻辑SQL执行的BEFORE_EXECUTE类型的事件
    OverallExecutionEvent event = new OverallExecutionEvent(sqlType, baseStatementUnits.size());    
    EventBusInstance.getInstance().post(event);
    // 将所有需要执行的SQL得到的Statement集合转换成迭代器，然后准备执行
    Iterator<? extends BaseStatementUnit> iterator = baseStatementUnits.iterator();
    // 第一个任务分离出来
    BaseStatementUnit firstInput = iterator.next();
    // 除第一个任务之外的任务异步执行（如果路由结果只有一条逻辑SQL，那么不需要异步执行）
    ListenableFuture<List<T>> restFutures = asyncExecute(sqlType, Lists.newArrayList(iterator), parameterSets, executeCallback);
    // 第一个任务的执行结果
    T firstOutput;
    // 除第一个任务外其他任务执行结果
    List<T> restOutputs;
    try {
        // 第一个任务同步执行
        firstOutput = syncExecute(sqlType, firstInput, parameterSets, executeCallback);
        // 取得其他异步执行任务的结果
        restOutputs = restFutures.get();
    } catch (final Exception ex) {
        event.setException(ex);                
        event.setEventExecutionType(EXECUTE_FAILURE);
        // 逻辑SQL如果执行失败，需要通过EventBus发送一个EXECUTE_FAILURE事件
        EventBusInstance.getInstance().post(event);
        ExecutorExceptionHandler.handleException(ex);
        return null;
    }
    event.setEventExecutionType(EXECUTE_SUCCESS);
    // 逻辑SQL如果执行成功，会发送一个“EXECUTE_SUCCESS”的事件。
    EventBusInstance.getInstance().post(event);
    List<T> result = Lists.newLinkedList(restOutputs);
    // 将第一个任务同步执行结果与其他任务异步执行结果合并就是最终的结果
    result.add(0, firstOutput);
    return result;
}
```

> EventBus原理以及失败后重试后面会单独一篇文章进行分析。这段代码里通过EventBus发送的执行事件是整个执行批次的事件，即用户执行的逻辑SQL事件，不论逻辑SQL路由成多少条实际SQL，都是一个批次。执行前发送BEFORE_EXECUTE事件；执行后，如果失败发送EXECUTE_FAILURE事件，如果成功发送EXECUTE_SUCCESS事件。至于执行实际SQL的事件，在后面分析的executeInternal()方法中发送。

OverallExecutionEvent 有几个重要属性：

1. sqlType：即sql类型，例如DQL，还有DML，DDL；
2. statementUnitSize：即有多少条需要执行的SQL；
3. id：代表此次需要执行的批次ID，生成规则就是UUD；
4. eventExecutionType：执行时间类型，申明为BEFORE_EXECUTE类型；

------

- 第一个任务同步执行其他任务异步执行的原因

考虑到分库分表后只需路由到一个数据源中的一个表的SQL占大多数（如果不是，说明分库分表的sharding列选取有问题），例如用户表根据user_id分库分表后，根据user_id查询用户信息。这种SQL全部**同步执行能节省线程开销**。后面从sharding-jdbc的作者张亮大神那里得到了证实。

### 异步执行

- 异步执行线程池

除第一个任务外的其他任务通过线程池异步执行，线程池构造法方式如下：

```
private final ListeningExecutorService executorService;
public ExecutorEngine(final int executorSize) {
    // 异步执行的线程池executorService是通过google-guava封装的线程池，设置了线程名称为增加了ShardingJDBC-***
    executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(
            executorSize, executorSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ShardingJDBC-%d").build()));
    // 增加了shutdown hook--应用关闭时最多等待60秒直到所有任务完成，从而在优雅停机过程中，尽量处理完遗留任务
    MoreExecutors.addDelayedShutdownHook(executorService, 60, TimeUnit.SECONDS);
}
```

- 异步执行核心

除第一个任务外的其他任务异步执行核心代码如下：

```
private <T> ListenableFuture<List<T>> asyncExecute(
        final SQLType sqlType, final Collection<BaseStatementUnit> baseStatementUnits, final List<List<Object>> parameterSets, final ExecuteCallback<T> executeCallback) {
    // 构造一个存放异步执行后的结果的list
    List<ListenableFuture<T>> result = new ArrayList<>(baseStatementUnits.size());
    final boolean isExceptionThrown = ExecutorExceptionHandler.isExceptionThrown();
    final Map<String, Object> dataMap = ExecutorDataMap.getDataMap();
    for (final BaseStatementUnit each : baseStatementUnits) {
        // 需要异步执行的所有任务，交给线程池处理
        result.add(executorService.submit(new Callable<T>() {      
            @Override
            public T call() throws Exception {
                return executeInternal(sqlType, each, parameterSets, executeCallback, isExceptionThrown, dataMap);
            }
        }));
    }
    // google-guava的方法--将所有异步执行结果转为list类型
    return Futures.allAsList(result);
}
```

- 同步执行核心代码

```
private <T> T syncExecute(... ...) throws Exception {
    return executeInternal(... ...);
}
```

### 执行核心

由同步执行核心代码和异步执行核心代码可知，最终都是调用`executeInternal()`，核心源码如下：

```
// 发布事件
List<AbstractExecutionEvent> events = new LinkedList<>();
if (parameterSets.isEmpty()) {
    // 构造无参SQL的事件（事件类型为BEFORE_EXECUTE）
    events.add(getExecutionEvent(sqlType, baseStatementUnit, Collections.emptyList()));
}
// 异步执行任务可能有多条实际SQL，这种情况下parameterSets的size不止1，所以，有多少实际SQL，就要发布多少事件
for (List<Object> each : parameterSets) {
    // 构造有参SQL的事件（事件类型为BEFORE_EXECUTE）
    events.add(getExecutionEvent(sqlType, baseStatementUnit, each));
}
// 调用google-guava的EventBus.post()提交“BEFORE_EXECUTE”执行前事件
for (AbstractExecutionEvent event : events) {
    EventBusInstance.getInstance().post(event);
}

try {
    // 执行SQL
    result = executeCallback.execute(baseStatementUnit);
} catch (final SQLException ex) {
    // 如果执行过程中抛出SQLException，即执行SQL失败，那么post一个EXECUTE_FAILURE类型的事件
    for (AbstractExecutionEvent each : events) {
        // 如果执行失败，则发布一个EXECUTE_FAILURE类型的时间
        each.setEventExecutionType(EXECUTE_FAILURE);
        each.setException(ex);
        EventBusInstance.getInstance().post(each);
        ExecutorExceptionHandler.handleException(ex);
    }
    return null;
}
for (AbstractExecutionEvent each : events) {
    // 如果执行成功，那么发布一个EXECUTE_SUCCESS类型的事件
    each.setEventExecutionType(EXECUTE_SUCCESS);
    EventBusInstance.getInstance().post(each);
}
```

分析这段源码可知：最终就是在目标数据库的目标表上执行`PreparedStatement`的`executeQuery|Update()`方法；且在执行前会利用google-guava的EventBus发布BEFORE_EXECUTE的事件（执行完成后，如果执行成功还会发布EXECUTE_SUCCESS事件，如果执行失败发布EXECUTE_FAILURE事件）。

接下来需要对并行执行后得到的结果集进行merge，示例SQL路由到两个表上执行，所以有2个执行结果，接下来的一篇文章会分析sharding-jdbc如何结果结果归并的；

### 执行总结

通过上面对执行过程的源码分析可知，sharding-jdbc整个执行过程还是比较简单的，主要有以下几步：

1. 任务分离，第1个任务同步执行，其他任务异步执行；
2. 整个逻辑SQL任务会发送执行前（BEFORE_EXECUTE）事件，和执行结果（EXECUTE_SUCCESS或者EXECUTE_FAILURE）的事件。
3. 每个实际SQL执行结果也会发送执行前事件和执行结果事件；
4. 监听到EXECUTE_FAILURE即执行失败的时间，会对SQL进行重试；
5. 将同步执行结果和异步执行结果合并到一个List中（结果合并就是合并这个List中每个表的执行结果）；

## 9. EventBus-轻量级发布订阅类库

说明：**EventBus**是google-guava提供的消息发布-订阅类库，3个最核心的方法如下：

1. **发布**：即post(Object)，发布事件到所有注册的订阅者，当事件被发布到所有订阅者后，这个方法就会返回成功，这个方法会忽略掉订阅者抛出的任何异常；
2. **注册**：即register(Object)；注册对象中所有订阅者方法，这些方法都能收到事件。
3. **解除注册**：即unregister(Object)；取消已注册对象中所有订阅者方法的注册；

------

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkEytFlYnVuORv11bIydQVJTTbj5p31gjhXj1JeziadjSiae9KKC0gNNM87AQlyyn2rNXB6kdV3lmMA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

sharding-jdbc使用EventBus发布&订阅时，对EventBus稍微封装了一下，即把EventBus设计为单例，然后通过EventBusInstance.getInstance()方法获取EventBus实例。EventBus单例核心源码如下：

```
public final class EventBusInstance {
    // 饿汉式实现单例模式 
    private static final EventBus INSTANCE = new EventBus();
    public static EventBus getInstance() {
        return INSTANCE;
    }
}
```

> **EventBus**全类名为`com.google.common.eventbus.EventBus`。

### EventBus介绍

**EventBus**来自于**google-guava**包中。源码注释如下：

```
Dispatches events to listeners, 
and provides ways for listeners to register themselves.
The EventBus allows publish-subscribe-style communication between components 
without requiring the components to explicitly 
register with one another (and thus be 
aware of each other).  
It is designed exclusively to replace traditional Java in-process event distribution using explicit registration. 
It is not a general-purpose publish-subscribe
system, nor is it intended for interprocess communication.
```

翻译：将事件分派给监听器，并为监听器提供注册自己的方法。**EventBus**允许组件之间的发布 - 订阅式通信，而不需要组件彼此明确注册（并且因此彼此意识到）。 它专门用于使用显式注册替换传统的Java进程内事件分发。 它不是一个通用的发布 - 订阅系统，也不是用于进程间通信。

### 使用参考

关于EventBus的用例代码提取自sharding-jdbc源码，并结合lombok最大限度的简化；

- DMLExecutionEvent

DMLExecutionEvent是发布&订阅事件的模型，并且有个父类BaseExecutionEvent，申明如下：

```
@Getter
@Setter
public class BaseExecutionEvent {
    private String id;
}

@Getter
@Setter
public class DMLExecutionEvent extends BaseExecutionEvent{
    private String dataSource;
    private String sql;
}
```

- DMLExecutionEventListener

即事件监听器，订阅者接收到发布的事件后，进行业务处理：

```
public final class DMLExecutionEventListener {
    @Subscribe
    @AllowConcurrentEvents
    public void listener(final DMLExecutionEvent event) {
        System.out.println("监听的DML执行事件: " + JSON.toJSONString(event));
        // do something
    }
}
```

- 发布&订阅

Main主方法中，注册订阅者监听事件，以及发布事件。

```
public class Main {
    static{
        // 注册监听器
        EventBusInstance.getInstance().register(new DMLExecutionEventListener());
    }

    public static void main(String[] args) throws Exception {
        // 循环发布10个事件
        for (int i=0; i<10; i++) {
            pub();
            Thread.sleep(1000);
        }
    }

    private static void pub(){
        DMLExecutionEvent event = new DMLExecutionEvent();
        event.setId(UUID.randomUUID().toString());
        event.setDataSource("sj_db_1");
        event.setSql("select * from t_order_0 where user_id=10");
        System.out.println("发布的DML执行事件: " + JSON.toJSONString(event));
        EventBusInstance.getInstance().post(event);
    }
}
```

### 源码分析

主要分析发布事件以及注册监听器的核心源码；

#### 注册源码分析

```
//注册Object上所有订阅方法，用来接收事件，上面的使用参考，DMLExecutionEventListener就是这里的object
public void register(Object object) {
    // 根据注册对象，找到所有订阅者（一个注册对象里可以申明多个订阅者）
    Multimap<Class<?>, EventSubscriber> methodsInListener =
            finder.findAllSubscribers(object);
    // 重入写锁保证线程安全
    subscribersByTypeLock.writeLock().lock();
    try {
        // 把订阅者信息放到map中缓存起来（发布事件post()时就会用到）
        subscribersByType.putAll(methodsInListener);
    } finally {
        // 重入琐写锁解锁
        subscribersByTypeLock.writeLock().unlock();
    }
}
```

说明：**Multimap**是guava自定义数据结构，类似`Map<K, Collection<V>>`，key就是事件类型，例如DMLExecutionEvent。value就是EventSubscriber即事件订阅者集合。需要注意的是，这个的订阅者集合是指Object里符合订阅者条件的所有方法。例如DMLExecutionEventListener.listener()，DMLExecutionEventListener中可以有多个订阅者，加上注解@Subscribe即可。

- **注册总结**

通过这段源码分析可知，注册的核心就是将注册对象中所有的订阅者信息缓存起来，方便接下来的发布过程找到订阅者。

#### 发布源码分析

```
public void post(Object event) {
    // 得到所有该类以及它的所有父类，父类的父类，直到Object（因为有些注册的监听器是监听其父类）
    Set<Class<?>> dispatchTypes = flattenHierarchy(event.getClass());

    boolean dispatched = false;
    // 遍历类本身以及所有父类
    for (Class<?> eventType : dispatchTypes) {
        // 重入读锁先锁住
        subscribersByTypeLock.readLock().lock();
        try {
            // 得到类的所有订阅者，例如DMLExecutionEvent的订阅者就是DMLExecutionEventListener（EventSubscriber有两个属性：重要的属性target和method，target就是监听器即DMLExecutionEventListener，method就是监听器方法即listener；从而知道DMLExecutionEvent这个事件由哪个类的哪个方法监听处理）
            Set<EventSubscriber> wrappers = subscribersByType.get(eventType);

            if (!wrappers.isEmpty()) {
                // 如果有订阅者，那么dispatched = true，表示该事件可以分发
                dispatched = true;
                // 遍历所有的订阅者，往每个订阅者的队列中都增加该事件
                for (EventSubscriber wrapper : wrappers) {
                    enqueueEvent(event, wrapper);
                }
            }
        } finally {
            // 解锁
            subscribersByTypeLock.readLock().unlock();
        }
    }
    // 如果没有订阅者，且发送的不是DeadEvent类型事件，那么强制发送一个DeadEvent类型事件。
    if (!dispatched && !(event instanceof DeadEvent)) {
        post(new DeadEvent(this, event));
    }

    // 分发进入队列的事件
    dispatchQueuedEvents();
}
```

enqueueEvent()&dispatchQueuedEvents()方法源码分析：

```
/** 
 * 核心数据结构为LinkedList，保存的是EventBus.EventWithSubscriber类型数据
 */
private final ThreadLocal<Queue<EventBus.EventWithSubscriber>> eventsToDispatch =
        new ThreadLocal<Queue<EventBus.EventWithSubscriber>>() {
            @Override protected Queue<EventBus.EventWithSubscriber> initialValue() {
                return new LinkedList<EventBus.EventWithSubscriber>();
            }
        };

void enqueueEvent(Object event, EventSubscriber subscriber) {
    // 数据结构为new LinkedList<EventWithSubscriber>()，EventWithSubscriber就是对event和subscriber的封装，LinkedList数据结构保证进入队列和消费队列顺序一致
    eventsToDispatch.get().offer(new EventBus.EventWithSubscriber(event, subscriber));
}

// 分发队列中的事件交给订阅者处理，这个过程称为drain，即排干。排干的过程中，可能有新的事件被追加到队列尾部
void dispatchQueuedEvents() {
    // 如果当前正在分发，则不重复执行
    if (isDispatching.get()) {
        return;
    }

    // 如果没有正在分发，那么利用ThreadLocal设置正在分发即isDispatching为true
    isDispatching.set(true);
    try {
        Queue<EventBus.EventWithSubscriber> events = eventsToDispatch.get();
        EventBus.EventWithSubscriber eventWithSubscriber;
        // 不断从Queue中取出任务处理（调用poll()方法）        
        while ((eventWithSubscriber = events.poll()) != null) {
            // 调用订阅者处理事件（method.invoke(target, new Object[] { event });，method和target来自订阅者）
            dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
        }
    } finally {
        // ThreadLocal可能内存泄漏，用完需要remove
        isDispatching.remove();
        // 队列中的事件任务处理完，清空队列，即所谓的排干（Drain）
        eventsToDispatch.remove();
    }
}
```

------

- 发布总结

总结一下调用了EventBus的post()方法后的流程：

1. 遍历发布对象本身以及所有父类，每个类都同等待遇；
2. 得到类的所有订阅者（监听器的有效方法集合）；
3. 如果有订阅者，就往订阅者的队列中增加事件；若果没有订阅者，并且发送的不是DeadEvent，那么强制发送DeadEvent；
4. 不断取出队列中的事件，交给订阅者处理。

------

- 引申使用

按照上面的分析，改写一下DMLExecutionEventListener ，增加到3个方法，每个方法都有Subscribe注解，并且第三个方法监听的是父类BaseExecutionEvent，三个方法都是有效的订阅者：

```
public class DMLExecutionEventListener {

    @Subscribe
    @AllowConcurrentEvents
    public void listener1(final DMLExecutionEvent event) {
        System.out.println("1. 监听的DML执行事件: " + JSON.toJSONString(event));
        // do something
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listener2(final DMLExecutionEvent event) {
        System.out.println("2. 监听的DML执行事件: " + JSON.toJSONString(event));
        // do something
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listener3(final BaseExecutionEvent event) {
        System.out.println("3. 监听的DML执行事件(Base): " + JSON.toJSONString(event));
        // do something
    }

}
```

## 10. sjdbc之结果合并（上）

前面已经分析到了SQL执行，这篇文章分析查询类型SQL的结果归并（INSERT, UPDATE, DELETE类型SQL的结果归并很简单，就是操作了多少张表，结果就是多少）；

ShardingPreparedStatement.executeQuery()核心源码如下：

```
@Override
public ResultSet executeQuery() throws SQLException {
    ResultSet result;
    try {
        // SQL路由，前面的文章已经分析过了
        Collection<PreparedStatementUnit> units = route();
        // SQL执行，也分析了
        List<ResultSet> resultSets = new PreparedStatementExecutor(...).executeQuery();
        // 这篇文章的重点--结果MERGE，即结果归并
        ResultSetMerger resultSetMerger = new MergeEngine(resultSets, (SelectStatement) routeResult.getSqlStatement()).merge();
            result = new ShardingResultSet(resultSets, resultSetMerger, this);
    } finally {
        clearBatch();
    }
    currentResultSet = result;
    return result;
}
```

由结果归并的代码可知，归并过程主要分为几个主要步骤：

1. 构造Merge引擎；
2. 对结果集进行merge；
3. 返回sharding结果集；
4. 调用ShardingResultSet的next方法取结果；







![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHlGTorBrOXdjqJ0ueX140PbmSSOpCGDXpUjmwWlulKNppupVmrfo7g2ia35wib7GMrmv89D4yGcfpfA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

接下来以示例SQL：`SELECT o.* FROM t_order o where o.user_id=10 order by o.order_id desc limit 2,3`分析sharding-jdbc的结果归并过程；

> 这个示例SQL会路由到多个实际表（数据节点），并且有ORDER BY，还有LIMIT。涉及的场景比较丰富，也是一些比较常用的场景。至于GROUP BY，比ORDER BY和LIMIT都要复杂，后面单独拽文分析。

### MergeEngine

构造Merge引擎源码如下：

```
// 由构造方法可知，MergeEngine主要有3个重要的属性
public MergeEngine(final List<ResultSet>, final SelectStatement) {
    // 第一个属性resultSets，就是各个实际表（数据节点）执行的结果集合（路由到多少个实际表，这个集合的size就有多大）
    this.resultSets = resultSets;
    // SQL申明，包含词法分析，SQL解析后的很多信息
    this.selectStatement = selectStatement;
    // 结果列名称以及序号，例如：{"ORDER_BY_DERIVED_0":4, "order_id":1, "status":3, "user_id":2}
    columnLabelIndexMap = getColumnLabelIndexMap(resultSets.get(0));
}
```

构造好MergeEngine后，就下来就会调用它的merge()方法：

```
public ResultSetMerger merge() throws SQLException {
    // columnLabelIndexMap就是上面分析的结果列名称以及序号
    selectStatement.setIndexForItems(columnLabelIndexMap);
    // 所以merge的核心是先调用build，再调用decorate()，让我们一个一个分析每个方法的用途
    return decorate(build());
}
```

#### build

这个方法的核心源码如下:

```
private ResultSetMerger build() throws SQLException {
    // 说明：这篇文章不分析GROUP BY的场景，因为它相比其他场景都要复杂，而且它有两种处理方式。所以，后面单独撰文分析。此处将部分代码省略
    if (!selectStatement.getGroupByItems().isEmpty() || !selectStatement.getAggregationSelectItems().isEmpty()) {
        ... ...
    }

    // 如果select语句中有order by字段，那么需要用OrderByStreamResultSetMerger对结果处理，即流式排序--sharding-jdbc非常重要的特性
    if (!selectStatement.getOrderByItems().isEmpty()) {
        return new OrderByStreamResultSetMerger(resultSets, selectStatement.getOrderByItems());
    }
    // 最后对结果用IteratorStreamResultSetMerger进行处理
    return new IteratorStreamResultSetMerger(resultSets);
}
```

根据这段代码可知，其作用是根据SQL语句依次选择多个不同的ResultSetMerger对结果进行合并处理，ResultSetMerger的类图如下：

![img](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHlGTorBrOXdjqJ0ueX140PbBYtYNAcQibMauCox8Jrnpicz8TQsuHvnonao7Ic8ObiaLkYP0dlic242pw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

由类关系图可知，它主要有5个重要的实现类：

- GroupByStreamResultSetMerger；
- GroupByMemoryResultSetMerger；
- OrderByStreamResultSetMerger；
- LimitDecoratorResultSetMerger；
- IteratorStreamResultSetMerger；

以示例SQL：`SELECT o.* FROM t_order o where o.user_id=10 order by o.order_id desc limit 2,3`为例，没有GROUP BY，但是有ORDER BY，还有LIMIT，所以需要用到OrderByStreamResultSetMerger和LimitDecoratorResultSetMerger对结果进行合并。至于IteratorStreamResultSetMerger，肯定会用到。

综上分析：build方法就是根据SQL语句的特性，选择不同的ResultSetMerger结果进行处理的过程。需要注意的build方法还不会选择LimitDecoratorResultSetMerger对结果进行处理，这是接下来要分析的decorate()要做的事情。

#### decorate

这个方法的核心源码如下：

```
private ResultSetMerger decorate(final ResultSetMerger resultSetMerger) throws SQLException {
    ResultSetMerger result = resultSetMerger;

    Limit limit = selectStatement.getLimit();
    if (null == limit) {
        // limit为空有两种情况，第一：sql语句中没有limit条件；第二，sql语句中的limit m,n不需要重写。因为这两种情况下，都不需要LimitDecoratorResultSetMerger对结果进行处理。
        return resultSetMerger;
    }

    // 如果SQL语句中有limit，还需要LimitDecoratorResultSetMerger配合进行结果归并（说明：Oracle,MySQL,SQLServer处理稍微有点不同, 这里只讲解MySQL，MySQL和PostgreSQL以及H2三种数据库的处理方法一样）；
    if (DatabaseType.MySQL == limit.getDatabaseType() 
    || DatabaseType.PostgreSQL == limit.getDatabaseType() 
    || DatabaseType.H2 == limit.getDatabaseType()) {
            return new LimitDecoratorResultSetMerger(resultSetMerger, selectStatement.getLimit());
        }
    return result;
}
```

> 所以，decorate方法就是根据SQL判断，是否需要使用LimitDecoratorResultSetMerger对结果进行处理。非常简单，SO EASY!

接下来将以执行示例SQL为例：
`SELECT o.* FROM t_order o where o.user_id=10 order by o.order_id desc limit 2,3`；
该SQL会被改写成：
`SELECT o.* , o.order_id AS ORDER_BY_DERIVED_0 FROM t_order_0 o where o.user_id=? order by o.order_id desc limit 2,3`；
一一讲解**OrderByStreamResultSetMerger**，**IteratorStreamResultSetMerger**和**LimitDecoratorResultSetMerger**，了解这几个ResultSetMerger的具体工作原理；

### OrderByStreamResultSetMerger

看命名就知道，这个类的作用是流式处理ORDER BY的结果集，其核心源码如下：

```
private final Queue<OrderByValue> orderByValuesQueue;

public OrderByStreamResultSetMerger(final List<ResultSet> resultSets, final List<OrderItem> orderByItems) throws SQLException {
    // sql中order by列的信息，实例sql是order by order_id desc，即此处就是order_id
    this.orderByItems = orderByItems;
    // 初始化一个优先级队列，优先级队列中的元素会根据OrderByValue中compareTo()方法排序，并且SQL重写后发送到多少个目标实际表，List<ResultSet>的size就有多大，Queue的capacity就有多大；
    this.orderByValuesQueue = new PriorityQueue<>(resultSets.size());
    // 将结果压入队列中
    orderResultSetsToQueue(resultSets);
    isFirstNext = true;
}

private void orderResultSetsToQueue(final List<ResultSet> resultSets) throws SQLException {
    // 遍历resultSets--在多少个目标实际表上执行SQL，该集合的size就有多大
    for (ResultSet each : resultSets) {
        // 将ResultSet和排序列信息封装成一个OrderByValue类型
        OrderByValue orderByValue = new OrderByValue(each, orderByItems);
        // 如果值存在，那么压入队列中
        if (orderByValue.next()) {
            orderByValuesQueue.offer(orderByValue);
        }
    }
    // 重置currentResultSet的位置：如果队列不为空，那么将队列的顶部(peek)位置设置为currentResultSet的位置
    setCurrentResultSet(orderByValuesQueue.isEmpty() ? resultSets.get(0) : orderByValuesQueue.peek().getResultSet());
}

@Override
public boolean next() throws SQLException {
    // 调用next()判断是否还有值, 如果队列为空, 表示没有任何值, 那么直接返回false
    if (orderByValuesQueue.isEmpty()) {
        return false;
    }
    // 如果队列不为空, 那么第一次一定返回true；即有结果可取（且将isFirstNext置为false，表示接下来的请求都不是第一次请求next()方法）
    if (isFirstNext) {
        isFirstNext = false;
        return true;
    }
    // 从队列中弹出第一个元素（因为是优先级队列，所以poll()返回的值，就是此次要取的值）
    OrderByValue firstOrderByValue = orderByValuesQueue.poll();
    // 如果它的next()存在，那么将它的next()再添加到队列中
    if (firstOrderByValue.next()) {
        orderByValuesQueue.offer(firstOrderByValue);
    }
    // 队列中所有元素全部处理完后就返回false
    if (orderByValuesQueue.isEmpty()) {
        return false;
    }
    // 再次重置currentResultSet的位置为队列的顶部位置；
    setCurrentResultSet(orderByValuesQueue.peek().getResultSet());
    return true;
}
```

### LimitDecoratorResultSetMerger

LimitDecoratorResultSetMerger核心源码如下：

```
public LimitDecoratorResultSetMerger(final ResultSetMerger resultSetMerger, final Limit limit) throws SQLException {
    super(resultSetMerger);
    // limit赋值（Limit对象包括limit m,n中的m和n两个值）
    this.limit = limit;
    // 判断是否需要跳过所有的结果项，即判断是否有符合条件的结果
    skipAll = skipOffset();
}

private boolean skipOffset() throws SQLException {
    // limit.getOffsetValue()就是得到offset的值，实例sql中为limit 2,3，所以offset=2
    for (int i = 0; i < limit.getOffsetValue(); i++) {
        // 尝试从OrderByStreamResultSetMerger生成的优先级队列中跳过offset个元素，如果.next()一直为true，表示有足够符合条件的结果，那么skipAll=false；否则没有足够符合条件的结果，那么skipAll=true；
        if (!getResultSetMerger().next()) {
            return true;
        }
    }
    // 如果构造了LimitDecoratorResultSetMerger，那么limit一定会被重写，所以rowNumber一定是0；
    rowNumber=0;
    // 能够运行到这里，表示还有符合条件的结果，所以返回false，即skipAll=false
    return false;
}

@Override
public boolean next() throws SQLException {
    // 如果skipAll为true，即跳过所有，表示没有任何符合条件的值，所以返回false，表示没有更多结果了（实际上这种情况是没有任何符合条件的结果）。
    if (skipAll) {
        return false;
    }
    if (limit.getRowCountValue() > -1) {
        // 每次调用next()获取一个值后，rowNumber自增，当自增rowCountValue（即limit m,n中的n）次后，就不能再往下继续取值了，因为条件limit m,n限制了取n个结果。
        return ++rowNumber <= limit.getRowCountValue() && getResultSetMerger().next();
    }
    return getResultSetMerger().next();
}
```

这里需要的注意的是，LimitDecoratorResultSetMerger构造方法中调用skipOffset()时，有两个作用：

1. 判断是否需要跳过所有的结果项，即判断是否有符合条件的结果；
2. 如果有足够的结果项，还需要跳过limit offset,rowCount中offset参数指定的结果项。

### IteratorStreamResultSetMerger

构造方法核心源码：

```
private final Iterator<ResultSet> resultSets;

public IteratorStreamResultSetMerger(final List<ResultSet> resultSets) {
    // 将List<ResultSet>改成Iterator<ResultSet>，方便接下来迭代取得结果；
    this.resultSets = resultSets.iterator();
    // 重置currentResultSet
    setCurrentResultSet(this.resultSets.next());
}
```

接下来再对这几个ResultSetMerger的作用进行**深入解读**和**示例讲解**。

------

假设运行示例SQL：`SELECT o.* FROM t_order o where o.user_id=10 order by o.order_id desc limit 2,3`，它会分发到两个目标实际表：
第一个实际表返回的结果是1，3，5，7，9；
第二个实际表返回的结果是2，4，6，8，10；
那么，经过OrderByStreamResultSetMerger的构造方法中的orderResultSetsToQueue()方法后，`Queue<OrderByValue> orderByValuesQueue`中初始时**只包含两个**OrderByValue，一个是10，一个是9；OrderByStreamResultSetMerger的构造过程到这里就完成了。

------

接下来就是LimitDecoratorResultSetMerger，由于示例SQL需要重写，所以会构造这个LimitDecoratorResultSetMerger对结果集进行处理。且如上分析可知，条件为limit 2,3，所以需要先调用next()跳过2个结果，这个过程为：

1. 跳过第1个结果时（isFirstNext=true）不需要特别的处理，只需要将isFirstNext置为false即可
2. 跳过第2个结果时，取得10，并将其从PriorityQueue中移除，然后补上10的next()，即8。然后执行orderByValuesQueue.offer(8)，这时候orderByValuesQueue中包含8和9，并且currentResultSet是9；

跳过2个结果后，接下来就是取3个值的过程：
每个表的其他结果通过调用ResultSet的next方法流式取出，实际上调用了OrderByStreamResultSetMerger里的next方法：

1. 取第1个结果时，先取得9，并将其从PriorityQueue中移除，然后补上9的next()，即7。然后执行orderByValuesQueue.offer(7)，这时候orderByValuesQueue中包含8和7，并且currentResultSet是8，即取得第1个结果8；
2. 取第2个结果时，先取得8，并将其从PriorityQueue中移除，然后补上8的next()，即6。然后执行orderByValuesQueue.offer(6)，这时候orderByValuesQueue中包含7和6，并且currentResultSet是7，即取得第2个结果7；
3. 取第3个结果时，先取得7，并将其从PriorityQueue中移除，然后补上7的next()，即5。然后执行orderByValuesQueue.offer(5)，这时候orderByValuesQueue中包含6和5，并且currentResultSet是6，即取得第3个结果6；
4. 取值数量已经达到3个（源码在LimitDecoratorResultSetMerger中的next()方法中），退出；

------

- 运行示意图

![流式处理过程](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHlGTorBrOXdjqJ0ueX140PbWpkCKRI8uHwhDJlCk3MdMW1AC6TNUiaz1gjnGWb32hgEKV7R3geqQiaw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)流式处理过程

![流式处理过程](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHlGTorBrOXdjqJ0ueX140PbabP9hnHRXKxB1vN1yEFhqTFTAULyyLHXFWicp6Yxia8FmVzN2TzK9D5w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)流式处理过程

## 11. sjdbc之结果合并（中）

在上一篇文章中已经分析了OrderByStreamResultSetMerger、LimitDecoratorResultSetMerger、IteratorStreamResultSetMerger，查看源码目录下ResultSetMerger的实现类，只剩下GroupByMemoryResultSetMerger和GroupByStreamResultSetMerger两个实现类的分析，接下来根据源码对两者的实现原理进行深入剖析；

### Memory Or Stream?

GroupBy有两个ResultSetMerge的实现：GroupByMemoryResultSetMerger和GroupByStreamResultSetMerger，那么如何选择呢？在MergeEngine中有一段这样的代码：

```
private ResultSetMerger build() throws SQLException {
    // 如果有group by或者聚合类型（例如sum, avg等）的SQL条件，就会选择一个GroupBy***ResultSetMerger
    if (!selectStatement.getGroupByItems().isEmpty() || !selectStatement.getAggregationSelectItems().isEmpty()) {
        // isSameGroupByAndOrderByItems()源码紧随其后
        if (selectStatement.isSameGroupByAndOrderByItems()) {
            return new GroupByStreamResultSetMerger(columnLabelIndexMap, resultSets, selectStatement);
        } else {
            return new GroupByMemoryResultSetMerger(columnLabelIndexMap, resultSets, selectStatement);
        }
    }
    // 这里的代码在上文中已经分析过，所以省略
}

// 判断选择哪个ResultSetMerger的核心逻辑
public boolean isSameGroupByAndOrderByItems() {
    // group by和order by的字段一样，就返回true
    return !getGroupByItems().isEmpty() && getGroupByItems().equals(getOrderByItems());
}
```

**源码解读**：
如果只有GROUP BY条件，没有ORDER BY，那么isSameGroupByAndOrderByItems()为true，例如：
`SELECT o.status,count(o.user_id) FROM t_order o where o.user_id=?  group by o.status`，因为这种sql会被重写为：
`SELECT o.status,count(o.user_id) FROM t_order_1 o where o.user_id=?  group by o.status  ORDER BY status ASC`，只有这种情况下GROUP BY和ORDER BY才是一致的（**注意重写结果**）。

**选择总结**：

- 如果有GROUP BY，没有ORDER BY，那么选择**GroupByMemoryResultSetMerger**；
- 如果有GROUP BY item，又有ORDER BY item ASC，一定是ASC，并且两者对应的字段一样都是item，那么选择**GroupByMemoryResultSetMerger**（由重写结果可以得知）；
- 其他情况都是选择**GroupByStreamResultSetMerger**；

接下来先分析**GroupByStreamResultSetMerger**是如何对结果进行GROUP BY聚合，假设数据源`dataSource_jdbc_0`中实际表`t_order_0`和实际表`t_order_1`的数据如下：

| order_id | user_id | status |
| :------- | ------: | :----: |
| 1000     |      10 |  INIT  |
| 1002     |      10 |  INIT  |
| 1004     |      10 | VALID  |
| 1006     |      10 |  NEW   |
| 1008     |      10 |  INIT  |

| order_id | user_id | status |
| :------- | ------: | :----: |
| 1001     |      10 |  NEW   |
| 1003     |      10 |  NEW   |
| 1005     |      10 | VALID  |
| 1007     |      10 |  INIT  |
| 1009     |      10 |  INIT  |

### GroupByStreamResultSetMerger

以执行SQL：`SELECT o.status, count(o.user_id) FROM t_order o where o.user_id=10  group by o.status`为例，分析**GroupByStreamResultSetMerger**：

1. 示例SQL只会路由到dataSource_jdbc_0数据源；
2. 示例SQL会路由到t_order_0和t_order_1两张表中；

------

- 构造方法

GroupByStreamResultSetMerger的构造方法核心源码如下：

```
public final class GroupByStreamResultSetMerger extends OrderByStreamResultSetMerger {  
    public GroupByStreamResultSetMerger(... ...) {
        // 调用父类OrderByStreamResultSetMerger的构造方法
        super(resultSets, selectStatement.getOrderByItems());
        // 标签(列名)和位置索引的map关系，例如{"COUNT(o.user_id)":2, "status":1}        
        this.labelAndIndexMap = labelAndIndexMap;
        // 执行的SQL语句申明，包含词法分析，SQL解析等信息
        this.selectStatement = selectStatement;
        currentRow = new ArrayList<>(labelAndIndexMap.size());
        // 如果优先级队列不为空，表示where条件中有group by，将队列中第一个元素的group值赋值给currentGroupByValues，即INIT（默认升序排列，即INIT > NEW > VALID，后面的聚合过程就是根据这个顺序来的）
        currentGroupByValues = getOrderByValuesQueue().isEmpty() ? Collections.emptyList() : new GroupByValue(getCurrentResultSet(), selectStatement.getGroupByItems()).getGroupValues();
    }
    ...
}
```

**说明**：GroupByStreamResultSetMerger的父类是OrderByStreamResultSetMerger，因为所有GROUP BY的SQL都会被重写为GROUP BY item ORDER BY item ASC。

- next方法

核心源码如下：

```
@Override
public boolean next() throws SQLException {
    currentRow.clear();
    // 如果优先级队列为空，表示没有任何结果，那么返回false
    if (getOrderByValuesQueue().isEmpty()) {
        return false;
    }
    if (isFirstNext()) {
        // 如果是第一次调用next()方法，那么调用父类OrderByStreamResultSetMerger的next方法处理
        super.next();
    }
    // 聚合的核心逻辑在这里
    if (aggregateCurrentGroupByRowAndNext()) {
        currentGroupByValues = new GroupByValue(getCurrentResultSet(), selectStatement.getGroupByItems()).getGroupValues();
    }
    return true;
}

private boolean aggregateCurrentGroupByRowAndNext() throws SQLException {
    boolean result = false;
    // selectStatement.getAggregationSelectItems()先得到select所有举行类型的项，例如select count(o.user_id) ***中聚合项是count(o.user_id)， 然后转化成map，key就是聚合项即o.user_id，value就是集合unit实例即AccumulationAggregationUnit；即o.user_id的COUNT集合计算是通过AccumulationAggregationUnit实现的，下面有对AggregationUnitFactory的分析
    Map<AggregationSelectItem, AggregationUnit> aggregationUnitMap = Maps.toMap(selectStatement.getAggregationSelectItems(), new Function<AggregationSelectItem, AggregationUnit>() {

        @Override
        public AggregationUnit apply(final AggregationSelectItem input) {
            return AggregationUnitFactory.create(input.getType());
        }
    });

    // 接下来准备聚合：如果GROUP BY的值相同，则进行聚合（
    // 例如第一个数据节点返回的status="init"和第二个数据节点返回的status="init"才能聚合。
    // 因为SQL可能会在多个数据源以及多个实际表上执行）
    while (currentGroupByValues.equals(new GroupByValue(getCurrentResultSet(), selectStatement.getGroupByItems()).getGroupValues())) {
        // 调用aggregate()方法进行聚合
        aggregate(aggregationUnitMap);
        cacheCurrentRow();
        // 调用next()方法，实际调用OrderByStreamResultSetMerger中的next()方法，currentResultSet会指向下一个元素；
        result = super.next();
        // 如果还有值，那么继续遍历
        if (!result) {
            break;
        }
    }
    setAggregationValueToCurrentRow(aggregationUnitMap);
    return result;
}
```

------

- AggregationUnitFactory

AggregationUnitFactory 源码如下，根据这段代码可知：

1. MAX和MIN聚合查询需要使用ComparableAggregationUnit；
2. SUM和COUNT需要使用AccumulationAggregationUnit；
3. AVG需要使用AverageAggregationUnit；



```
switch (type) {
    case MAX:
        return new ComparableAggregationUnit(false);
    case MIN:
        return new ComparableAggregationUnit(true);
    case SUM:
    case COUNT:
        return new AccumulationAggregationUnit();
    case AVG:
        return new AverageAggregationUnit();
    default:
        throw new UnsupportedOperationException(type.name());
}
```

> 由这段源码可知，目前sharding-jdbc只max，min，sum，count，avg这些聚合类型操作，DISTINCT暂不支持。

- aggregate

aggregate()的核心就是根据聚合类型，通过AggregationUnitFactory 得到AggregationUnit实现类，然后调用AggregationUnit实现类中的merge()方法。

由于这次的示例SQL是COUNT聚合操作，所以调用AccumulationAggregationUnit.merge()方法`--`实现很简单，就是不断累加得到最终结果。例如t_order_0得到的结果中statu="INIT"的count(o.user_id)=3，而t_order_1得到的结果中statu="INIT"的count(o.user_id)=2，累加后就是5。

后面会对每个AggregationUnit实现类进行简单总结和分析。

------

### 图解执行过程

这一块的代码逻辑稍微有点复杂，下面通过示意图分解执行过程，让sharding-jdbc执行GROUP BY的整个过程更加清晰：

**step1**. SQL执行

首先在两个实际表`t_order_0`和`t_order_1`中分别执行SQL：`SELECT o.status, count(o.user_id) FROM t_order o where o.user_id=10  group by o.status ORDER BY status ASC`，`t_order_0`和`t_order_1`分别得到如下的结果（重要说明：每个实际表的返回结果已经根据status字段进行升序排列）：

| status | count(o.user_id) |
| :----- | :--------------: |
| INIT   |        3         |
| NEW    |        1         |
| VALID  |        1         |

| status | count(o.user_id) |
| :----- | :--------------: |
| INIT   |        2         |
| NEW    |        2         |
| VALID  |        1         |

**step2**. OrderByStreamResultSetMerger处理

即在GroupByStreamResultSetMerger中调用OrderByStreamResultSetMerger的构造方法从而得到优先级队列，初始化得到的优先级队列如下图所示，优先级队列中仅包含两个元素[(INIT, 3), (INIT 2)]`--`**优先级队列不会保存实际表的所有结果，这是sharding-jdbc流式处理的精髓**。这样做的好处是能最大限度节省内存，防止OOM：

![初始化构造的优先级队列](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHkbusjN22uB9gSv1RHY09FgOfsYDhwWJkTFI8nmGJ4NPrVRQENfnyj8Z8tnOXheAwzrlBOzrBiboNQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)初始化构造的优先级队列

**step3**. Map

通过转换得到`Map<AggregationSelectItem, AggregationUnit>`，key就是聚合的KEY，例如count(user_id)；value就是聚合计算的AggregationUnit实现类，例如AccumulationAggregationUnit，通过它不断计算聚合结果；

由于示例SQL语句中只有COUNT(o.user_id)涉及到聚合运行，所以这个map的size为1，key是count(user_id)，value是AccumulationAggregationUnit；

如果SQL是：
`SELECT o.status, count(o.user_id), max(order_id) FROM t_order o where o.user_id=?  group by o.status`，那么map的size为2，且：
第一个entry的key是count(user_id)，value是AccumulationAggregationUnit；
第二个entry的key是max(order_id)，value是ComparableAggregationUnit；

**step4**. 循环遍历&结果合并

最后一步核心代码如下，聚合计算过程为：

1. 调用AccumulationAggregationUnit中的merge方法，对(INIT, 3)和(INIT, 2)进行合并，从而得到(INIT, 5)；
2. 调用AccumulationAggregationUnit中的merge方法，对(NEW, 1)和(NEW, 2)进行合并，从而得到(NEW, 3)；
3. 调用AccumulationAggregationUnit中的merge方法，对(VALID, 1)和(VALID, 1)进行合并，从而得到(VALID, 2)；
4. 得到最终的结果就是[(INIT, 5), (NEW, 3), (VALID, 2)]。



```
// 取得匹配的列再聚合计算。比如(INIT, 3)和(INIT, 2)聚合计算完成后，不能与(NEW, 1)进行聚合计算
while (currentGroupByValues.equals(new GroupByValue(getCurrentResultSet(), selectStatement.getGroupByItems()).getGroupValues())) {
    aggregate(aggregationUnitMap);
    cacheCurrentRow();
    result = super.next();
    // 没有更多数据，则聚合计算完成
    if (!result) {
        break;
    }
}
```

如下图所示：

1. 先聚合计算(INIT,3)和(INIT,2)得到(INIT,5)；
2. 再聚合计算(NEW,1)和(NEW,2)得到(NEW,3)；
3. 最后聚合计算(VALID,1)和(VALID,1)得到(VALID,2)；
4. 聚合计算完成；

![powered by afei](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHkbusjN22uB9gSv1RHY09FgldSpezd6KRAVIc6Hjo0f8UmTU2x0ttIicDak0vW2bCEf3MibglRK6UQg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)powered by afei

### AggregationUnit说明

AggregationUnit即聚合计算接口，总计有三个实现类：

1. AccumulationAggregationUnit
2. ComparableAggregationUnit
3. AverageAggregationUnit

接下来分别对其简单介绍；

#### AccumulationAggregationUnit

实现源码如下，SUN和COUNT两个聚合计算都是用这个AggregationUnit实现，核心实现就是累加：

```
@Override
public void merge(final List<Comparable<?>> values) {
    if (null == values || null == values.get(0)) {
        return;
    }
    if (null == result) {
        result = new BigDecimal("0");
    }
    // 核心实现代码：累加
    result = result.add(new BigDecimal(values.get(0).toString()));
    log.trace("Accumulation result: {}", result.toString());
}
```

#### ComparableAggregationUnit

实现源码如下，MAX和MIN两个聚合计算都是用这个AggregationUnit实现，核心实现就是比较：

```
@Override
public void merge(final List<Comparable<?>> values) {
    if (null == values || null == values.get(0)) {
        return;
    }
    if (null == result) {
        result = values.get(0);
        log.trace("Comparable result: {}", result);
        return;
    }
    // 新的值与旧的值比较大小
    int comparedValue = ((Comparable) values.get(0)).compareTo(result);
    // 升序和降序比较方式不同（max聚合计算时asc为false，min聚合计算时asc为true），min聚合计算时找一个更小的值（asc && comparedValue < 0），max聚合计算时找一个更大的值（!asc && comparedValue > 0）
    if (asc && comparedValue < 0 || !asc && comparedValue > 0) {
        result = values.get(0);
        log.trace("Comparable result: {}", result);
    }
}
```

#### AverageAggregationUnit

实现源码如下，AVG聚合计算就是用的这个AggregationUnit实现，核心实现是将AVG转化后的SUM/COUNT，累加得到总SUM和总COUNT相除就是最终的AVG结果；

```java
@Override
public void merge(final List<Comparable<?>> values) {
    if (null == values || null == values.get(0) || null == values.get(1)) {
        return;
    }
    if (null == count) {
        count = new BigDecimal("0");
    }
    if (null == sum) {
        sum = new BigDecimal("0");
    }
    // COUNT累加 
    count = count.add(new BigDecimal(values.get(0).toString()));
    // SUM累加
    sum = sum.add(new BigDecimal(values.get(1).toString()));
    log.trace("AVG result COUNT: {} SUM: {}", count, sum);
}
```

## 12. sjdbc之结果合并（下）

在上一篇文章中主要分析了sharding-jdbc如何在**GroupByStreamResultSetMerger**和**GroupByMemoryResultSetMerger**中选择，并分析了**GroupByStreamResultSetMerger**的实现原理，接下来分析**GroupByMemoryResultSetMerger**的实现原理；

通过上一篇文章的分析可知，选择逻辑如下：

- 如果有GROUP BY，没有ORDER BY，那么选择GroupByMemoryResultSetMerger；
- 如果有GROUP BY item，又有ORDER BY item ASC，一定是ASC，并且两者对应的字段一样都是item，那么选择GroupByMemoryResultSetMerger（由重写结果可以得知）；
- 其他情况都是选择GroupByStreamResultSetMerger；

所以，只要构造一条SQL：**GROUP BY 和 ORDER BY的字段不一样**，就会需要GroupByStreamResultSetMerger对结果进行归并（或者GROUP BY和ORDER 的字段一样，但是ORDER BY item DESC），例如：

```
SELECT o.status,count(o.user_id) count_user_id 
FROM t_order o where o.user_id=? 
group by o.status order by count_user_id asc
```

### 源码分析

构造SQL后，接下来通过源码深入分析sharding-jdbc如何利用GroupByStreamResultSetMerger对结果集进行结果归并。

- 构造方法

GroupByMemoryResultSetMerger的构造方法源码如下：

```
public GroupByMemoryResultSetMerger(...) throws SQLException {
    // labelAndIndexMap就是select结果列与位置索引的map，例如{count_user_id:2， status:1}
    super(labelAndIndexMap);
    // select查询语句申明，包括SQL语句，词法分析，解析结果等信息
    this.selectStatement = selectStatement;
    // resultSets就是在多个实际表（数据节点）并发执行返回的结果集合，在多少个实际表上执行，resultSets的size就有多大；
    memoryResultSetRows = init(resultSets);
}
```

------

- 附执行结果

在实际表t_order_0上执行SQL返回的结果如下：

| status | count_user_id |
| :----: | :-----------: |
|  NEW   |       1       |
| VALID  |       1       |
|  INIT  |       3       |

在实际表t_order_1上执行SQL返回的结果如下：

| status | count_user_id |
| :----: | :-----------: |
| VALID  |       1       |
|  INIT  |       2       |
|  NEW   |       2       |

说明：首先知道实际表的返回结果，后面的分析更容易理解；

- init()方法分析

构造方法中的init方法源码如下：

```
private Iterator<MemoryResultSetRow> init(final List<ResultSet> resultSets) throws SQLException {
    Map<GroupByValue, MemoryResultSetRow> dataMap = new HashMap<>(1024);
    Map<GroupByValue, Map<AggregationSelectItem, AggregationUnit>> aggregationMap = new HashMap<>(1024);
    // 遍历多个实际表执行返回的结果集合中所有的结果，即2个实际表，所以resultSets的size为2。并且每个实际表3条结果，总计6条结果.
    for (ResultSet each : resultSets) {
        // each就是遍历过程中的一条结果，
        while (each.next()) {
            // selectStatement.getGroupByItems()就是GROUP BY列，即status，将结果和GROUP BY列组成一个GroupByValue对象--实际是从ResultSet中取出GROUP BY列的值，例如NEW，VALID，INIT等
            GroupByValue groupByValue = new GroupByValue(each, selectStatement.getGroupByItems());
            // initForFirstGroupByValue()分析如下
            initForFirstGroupByValue(each, groupByValue, dataMap, aggregationMap);
            // 聚合过程
            aggregate(each, groupByValue, aggregationMap);
        }
    }
    // 将aggregationMap中的聚合计算结果封装到dataMap中
    setAggregationValueToMemoryRow(dataMap, aggregationMap);
    // 将结果转换成List<MemoryResultSetRow>形式
    List<MemoryResultSetRow> result = getMemoryResultSetRows(dataMap);
    if (!result.isEmpty()) {
        // 如果有结果，再将currentResultSetRow置为List<MemoryResultSetRow>的第一个元素。后面再遍历得到合并后的结果时，就会从第一个结果开始遍历
        setCurrentResultSetRow(result.get(0));
    }
    // 返回List<MemoryResultSetRow>的迭代器，后面的取结果，实际上就是迭代这个集合；
    return result.iterator();
}   
```

------

- initForFirstGroupByValue源码分析

这个方法的源码如下：

```
private void initForFirstGroupByValue(...) throws SQLException {
    // groupByValue如果是第一次出现，那么在dataMap中初始化一条数据，key就是groupByValue，例如NEW；value就是new MemoryResultSetRow(resultSet)，即将ResultSet中的结果取出来封装到MemoryResultSetRow中，MemoryResultSetRow实际就一个属性Object[] data，那么data值就是这样的["NEW", 1]                              
    if (!dataMap.containsKey(groupByValue)) {
        dataMap.put(groupByValue, new MemoryResultSetRow(resultSet));
    }
    // groupByValue如果是第一次出现，那么在aggregationMap中初始化一条数据，key就是groupByValue，例如NEW；value又是一个map，这个map的key就是select中有聚合计算的列，例如count(user_id)，即count_user_id；value就是AggregationUnit的实现，count聚合计算的实现是AccumulationAggregationUnit
    if (!aggregationMap.containsKey(groupByValue)) {
        Map<AggregationSelectItem, AggregationUnit> map = Maps.toMap(selectStatement.getAggregationSelectItems(), new Function<AggregationSelectItem, AggregationUnit>() {
            @Override
            public AggregationUnit apply(final AggregationSelectItem input) {
                // 根据聚合计算类型得到AggregationUnit的实现
                return AggregationUnitFactory.create(input.getType());
            }
        });
        aggregationMap.put(groupByValue, map);
    }
}
```

总结：这个方法都是为了接下来的聚合计算做准备工作；

- aggregate聚合

aggregate()源码如下--即在内存中将多个实际表中返回的结果进行聚合：

```
private void aggregate(final ResultSet resultSet, final GroupByValue groupByValue, final Map<GroupByValue, Map<AggregationSelectItem, AggregationUnit>> aggregationMap) throws SQLException {
    // 遍历select中所有的聚合类型，例如COUNT(o.user_id)
    for (AggregationSelectItem each : selectStatement.getAggregationSelectItems()) {
        List<Comparable<?>> values = new ArrayList<>(2);
        if (each.getDerivedAggregationSelectItems().isEmpty()) {
            values.add(getAggregationValue(resultSet, each));
        } else {
            for (AggregationSelectItem derived : each.getDerivedAggregationSelectItems()) {
                values.add(getAggregationValue(resultSet, derived));
            }
        }
        // 通过AggregationUnit实现类即AccumulationAggregationUnit进行聚合，实际上就是聚合本次遍历到的ResultSet，聚合的临时结果就在AccumulationAggregationUnit的属性result中（AccumulationAggregationUnit聚合的本质就是累加）
        aggregationMap.get(groupByValue).get(each).merge(values);
    }
}
```

整个聚合过程就是经过下面这段核心代码：

```
for (ResultSet each : resultSets) { 
    while (each.next()) { 
        ...
    }
} 
```

遍历所有结果并聚合计算后，aggregationMap这个map中已经聚合计算完后的结果，如下所示：

```
{
    "VALID": {
        "COUNT(user_id)": 2
    },
    "INIT": {
        "COUNT(user_id)": 5
    },
    "NEW": {
        "COUNT(user_id)": 3
    }
}
```

再将aggregationMap中的结果封装到`Map<GroupByValue, MemoryResultSetRow> dataMap`这个map中，结果形式如下所示：

```
{
    "VALID": ["VALID", 2],
    "INIT": ["INIT", 5],
    "NEW": ["NEW", 3]
}
```

> MemoryResultSetRow的本质就是一个`Object[] data`，所以其值是["VALID", 2]，["INIT", 5]这种形式

最后将结果转成`List<MemoryResultSetRow>`，并且排序--如果有ORDER BY，那么根据ORDER BY的值进行排序，否则根据GROUP BY的值排序（因为GROUP BY item会被重写为GROUP BY item ORDER BY item ASC）：

```
private List<MemoryResultSetRow> getMemoryResultSetRows(final Map<GroupByValue, MemoryResultSetRow> dataMap) {
    List<MemoryResultSetRow> result = new ArrayList<>(dataMap.values());
    // 通过GroupByRowComparator这个自定义的比较器进行排序
    Collections.sort(result, new GroupByRowComparator(selectStatement));
    return result;
}

// 自定义比较器源码如下：
public final class GroupByRowComparator implements Comparator<MemoryResultSetRow> {

    @Override
    public int compare(final MemoryResultSetRow o1, final MemoryResultSetRow o2) {
        // 如果有ORDER BY item这样的条件，那么结果需要根据item排序。
        if (!selectStatement.getOrderByItems().isEmpty()) {
            return compare(o1, o2, selectStatement.getOrderByItems());
        }
        // 如果没有有ORDER BY x这样的条件，那么结果需要根据GROUP BY y的这个y进行排序。
        return compare(o1, o2, selectStatement.getGroupByItems());
    }
    ...
}    
```

到这里，**GroupByMemoryResultSetMerger**即内存GROUP聚合计算已经分析完成，依旧通过运行过程图解加深对**GroupByMemoryResultSetMerger**的理解，运行过程图如下图所示：

![GroupByMemoryResultSetMerger by afei](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHmjhziaUicCp71eYegF8YOTsQzpCnacqaDl0m9gZPuRASyXYZW1DkDLp4zXTl8ViakTY8ibBq5TgXhqhA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)GroupByMemoryResultSetMerger by afei

![GroupByMemoryResultSetMerger by afei](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHmjhziaUicCp71eYegF8YOTsQOfydAfVlDgJ5ibibiaJob684PpESe3Pc8gU7yogRMvJ9pu5bzhIf5qLBA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)GroupByMemoryResultSetMerger by afei

------

### GroupByMemoryResultSetMerger总结

正如**GroupByMemoryResultSetMerger**的名字一样，其实现原理是把所有结果加载到内存中，在内存中进行计算，而**GroupByStreamResultSetMerger**是流式计算方法，并不需要加载所有实际表返回的结果到内存中。这样的话，如果SQL返回的总结果数比较多，**GroupByMemoryResultSetMerger**的处理方式就可能会撑爆内存；这个是使用sharding-jdbc一个非常需要注意的地方；

### 结果归并总结

通过sjdbc之结果归并上，中，下三篇文章的分析，接下来对sharding-jdbc的结果归并进行总结。

#### 性能瓶颈

查询偏移量过大的分页会导致数据库获取数据性能低下，以MySQL为例：

```
SELECT * FROM t_order ORDER BY id LIMIT 1000000, 10
```

这句SQL会使得MySQL在无法利用索引的情况下跳过1000000条记录后，再获取10条记录，其性能可想而知。而在分库分表的情况下（假设分为2个库），为了保证数据的正确性，SQL会改写为：

```
SELECT * FROM t_order ORDER BY id LIMIT 0, 1000010
```

即将偏移量前的记录全部取出，并仅获取排序后的最后10条记录。这会在数据库本身就执行很慢的情况下，进一步加剧性能瓶颈。因为原SQL仅需要传输10条记录至客户端，而改写之后的SQL则会传输1000010*2的记录至客户端。

#### Sharding-JDBC的优化

Sharding-JDBC进行了2个方面的优化。

首先，Sharding-JDBC采用流式处理 + 归并排序的方式来避免内存的过量占用。Sharding-JDBC的SQL改写，不可避免的占用了额外的带宽，但并不会导致内存暴涨。

与直觉不同，大多数人认为Sharding-JDBC会将1000010*2记录全部加载至内存，进而占用大量内存而导致内存溢出。
但由于每个结果集的记录是有序的，因此Sharding-JDBC每次比较仅获取各个分片的当前结果集记录，驻留在内存中的记录仅为当前路由到的分片的结果集的当前游标指向而已。
对于本身即有序的待排序对象，归并排序的时间复杂度仅为O(n)，性能损耗很小。

其次，Sharding-JDBC对仅落至单分片的查询进行进一步优化。落至单分片查询的请求并不需要改写SQL也可以保证记录的正确性，因此在此种情况下，Sharding-JDBC并未进行SQL改写，从而达到节省带宽的目的。

#### 更好的分页解决方案

由于LIMIT并不能通过索引查询数据，因此如果可以保证ID的连续性，通过ID进行分页是比较好的解决方案：

```
SELECT * FROM t_order WHERE id > 100000 AND id <= 100010 ORDER BY id
```

或通过记录上次查询结果的最后一条记录的ID进行下一页的查询：

```
SELECT * FROM t_order WHERE id > 100000 LIMIT 10
```

摘自：sharding-jdbc使用指南☞分页及子查询：http://shardingjdbc.io/1.x/docs/02-guide/subquery/

#### 是否需要这种分页

无论是`SELECT * FROM t_order ORDER BY id LIMIT 0, 100010`或者`SELECT * FROM t_order WHERE id > 100000 LIMIT 10`，性能都一般般，后者只是稍微好点而已，但是由于LIMIT的存在，结果并不非常理想（可以通过EXPLAIN第二条SQL查看代价验证）；

是否能从产品角度或者用户习惯等方面解决或者避免这个问题？

比如我们以前有个**每日TOP榜单**需求，分析用户行为一般不会无限制往下滑，即使有这种用户，也是极少数，可以忽略。这样的话，可以通过SQL`select * from t_apps order by download_times desc LIMIT 300`只查询TOP榜单的前10页总计300个TOP应用，然后把这些数据以list结构保存到redis中。这样的话，用户查看**每日TOP榜单**只需通过`LRANGE key start stop`从redis缓存中取数据即可，且限制API查询条件offset不允许超过300；

一些更牛逼的项目，TOP榜单可能不是一条简单的SQL就能搞定。需要结合商业广告，竞价排名，蹿升速度（例如子弹短信），用户已安装APP等信息，通过大数据得出千人千面的TOP榜单。原理还是一样，算出TOP300榜单后，缓存到redis中，API直接取数据即可。

## 13. sjdbc之最大努力型分布式事务

关于sharding-jdbc分布式事务：

- Best efforts delivery transaction (已经实现).
- Try confirm cancel transaction (待定).

> Sharding-JDBC由于性能方面的考量，决定不支持强一致性分布式事务。

### 最大努力送达型事务说明

Best efforts delivery transaction就是最大努力送达型事务。在分布式数据库的场景下，相信对于该数据库的操作最终一定可以成功，所以通过最大努力送达，反复尝试。

很少有公司分布式场景下用强一致性事务。就笔者和陆金所朋友沟通，他们保证数据一致性的核心是T+1对账。再比如带有NFC功能的手机可以给公交卡充值的场景，笔者做过试验，在微信支付扣费后，将公交卡挪开，这时候会导致充值失败，但是资金已经扣除。微信支付的做法是T+1对账后将资金返回给用户，微信支付并不会在充值失败后一段时间内马上将资金返回给用户。

### 最大努力送达型事务架构图

![最大努力送达型事务的架构图](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHmYHq8W7PBN7yKCztjQIiah7xMicCz687sMwGfD6joT1VGwm3EqUwiaomDQaMsfFBiaSCH4AzRlpEJ6wQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)最大努力送达型事务的架构图

> 摘自sharding-jdbc使用指南☞事务支持：http://shardingjdbc.io/1.x/docs/02-guide/transaction/

解读这张架构图，对几个重要的执行过程进行更详细的说明：

1. **执行前**。执行前事件->记录事务日志。sharding-jdbc对于任何执行，都会先记录事务日志。
2. **执行成功**。执行结果事件->监听执行事件->执行成功->清理事务日志。如果执行成功，就会清理事务日志。
3. **执行失败，同步重试成功**。执行结果事件->监听执行事件->执行失败->重试执行->执行成功->清理事务日志。
4. **执行失败，同步重试失败，异步重试成功**。执行结果事件->监听执行事件->执行失败->重试执行->执行失败->"异步送达作业"重试执行->执行成功->清理事务日志
5. **执行失败，同步重试失败，异步重试失败，事务日志保留**----如图所示，执行结果事件->监听执行事件->执行失败->重试执行->执行失败->"异步送达作业"重试执行->执行失败->… …

说明：sharding-jdbc在执行前都会通过**执行前事件**来**记录事务日志**；执行事件类型包括3种：

- **BEFORE_EXECUTE**；
- **EXECUTE_FAILURE**；
- **EXECUTE_SUCCESS**；

------

- 同步

另外，这里的**同步**不是绝对的同步执行，而是通过google-guava的EventBus订阅执行事件，在监听端判断是EXECUTE_FAILURE事件，然后最多重试`syncMaxDeliveryTryTimes`次。后面对`BestEffortsDeliveryListener`的源码分析有介绍；

- 异步

这里的**异步**通过外挂程序实现，本质就是一个基于elastic-job的分布式JOB任务，在下一篇文章会有分析；

### 使用限制

- 使用最大努力送达型柔性事务的SQL需要满足幂等性。
- INSERT语句要求必须包含主键，且不能是自增主键。
- UPDATE语句要求幂等，不能是UPDATE table SET x=x+1。
- DELETE语句无要求。

### 开发示例

```
// 1. 配置SoftTransactionConfiguration
SoftTransactionConfiguration transactionConfig = new SoftTransactionConfiguration(dataSource);
// 配置相关请看后面的备注
transactionConfig.setXXX();

// 2. 初始化SoftTransactionManager
SoftTransactionManager transactionManager = new SoftTransactionManager(transactionConfig);
transactionManager.init();

// 3. 获取BEDSoftTransaction
BEDSoftTransaction transaction = (BEDSoftTransaction) transactionManager.getTransaction(SoftTransactionType.BestEffortsDelivery);

// 4. 开启事务
transaction.begin(connection);

// 5. 执行JDBC
// code here

// 6.关闭事务
transaction.end();
```

------

备注：SoftTransactionConfiguration支持的配置以及含义请参考官方文档sharding-jdbc使用指南☞事务支持：http://shardingjdbc.io/docs/02-guide/transaction/，这段开发示例的代码也摘自官方文档；也可参考`sharding-jdbc-transaction`模块中`io.shardingjdbc.transaction.integrate.SoftTransactionTest`如何使用柔性事务，**但是这里的代码需要稍作修改，否则只是普通的执行逻辑，不是sharding-jdbc的执行逻辑**：

```
@Test
public void bedSoftTransactionTest() throws SQLException {
    SoftTransactionManager transactionManagerFactory = new SoftTransactionManager(getSoftTransactionConfiguration(getShardingDataSource()));
    // 初始化柔性事务管理器
    transactionManagerFactory.init();
    BEDSoftTransaction transactionManager = (BEDSoftTransaction) transactionManagerFactory.getTransaction(SoftTransactionType.BestEffortsDelivery);
    transactionManager.begin(getShardingDataSource().getConnection());
    // 执行INSERT SQL（DML类型），如果执行过程中异常，会在`BestEffortsDeliveryListener`中重试
    insert();
    transactionManager.end();
}

private void insert() {
    String dbSchema = "insert into transaction_test(id, remark) values (2, ?)";
    try (
            // 将.getConnection("db_trans", SQLType.DML)移除，这样的话，得到的才是ShardingConnection 
            Connection conn = getShardingDataSource().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
        preparedStatement.setString(1, "JUST TEST IT .");
        preparedStatement.executeUpdate();
    } catch (final SQLException e) {
        e.printStackTrace();
    }
}
```

### 核心源码分析

通过 **sjdbc源码之路由&执行** 中对**ExecutorEngine**的分析可知，sharding-jdbc在执行SQL前后，都会调用`EventBus.post()`发布执行事件。那么调用`EventBusInstance.register()`的地方，就是柔性事务处理的地方。而sharding-jdbc在`SoftTransactionManager.init()`中调用了`EventBus.register()`初始化注册事件，所以柔性事务实现的核心在**SoftTransactionManager**这里。

#### 柔性事务管理器

柔性事务实现在`SoftTransactionManager`中，核心源码如下：

```
public final class SoftTransactionManager {

    // 柔性事务配置对象   
    @Getter
    private final SoftTransactionConfiguration transactionConfig;

    /**
     * Initialize B.A.S.E transaction manager.
     * @throws SQLException SQL exception
     */
    public void init() throws SQLException {
        // 初始化注册最大努力送达型柔性事务监听器；
        EventBusInstance.getInstance().register(new BestEffortsDeliveryListener());
        if (TransactionLogDataSourceType.RDB == transactionConfig.getStorageType()) {
            // 如果事务日志数据源类型是关系型数据库，则创建事务日志表transaction_log
            createTable();
        }
        // 内嵌的最大努力送达型异步JOB任务，依赖当当开源的elastic-job
        if (transactionConfig.getBestEffortsDeliveryJobConfiguration().isPresent()) {
            new NestedBestEffortsDeliveryJobFactory(transactionConfig).init();
        }
    }

    // 从这里可知创建的事务日志表表名是transaction_log（所以需要保证每个库中用户没有自定义创建transaction_log表）
    private void createTable() throws SQLException {
        String dbSchema = "CREATE TABLE IF NOT EXISTS `transaction_log` ("
                + "`id` VARCHAR(40) NOT NULL, "
                + "`transaction_type` VARCHAR(30) NOT NULL, "
                + "`data_source` VARCHAR(255) NOT NULL, "
                + "`sql` TEXT NOT NULL, "
                + "`parameters` TEXT NOT NULL, "
                + "`creation_time` LONG NOT NULL, "
                + "`async_delivery_try_times` INT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY (`id`));";
        try (
                Connection conn = transactionConfig.getTransactionLogDataSource().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
            preparedStatement.executeUpdate();
        }
    }
```

从这段源码可知，柔性事务的几个重点如下，接下来一一根据源码进行分析；

- 事务日志存储器；
- 最大努力送达型事务监听器；
- 异步送达JOB任务；

#### 1.事务日志存储器

柔性事务日志接口类为`TransactionLogStorage.java`，有两个实现类：

1. **RdbTransactionLogStorage**：关系型数据库存储柔性事务日志；
2. **MemoryTransactionLogStorage**：内存存储柔性事务日志；

------

- 1.1事务日志核心接口

TransactionLogStorage中几个重要接口在两个实现类中的实现：

- **void add(TransactionLog)**：Rdb实现就是把事务日志TransactionLog 插入到`transaction_log`表中，Memory实现就是把事务日志保存到`ConcurrentHashMap`中；
- **void remove(String id)**：Rdb实现就是从`transaction_log`表中删除事务日志，Memory实现从`ConcurrentHashMap`中删除事务日志；
- **void increaseAsyncDeliveryTryTimes(String id)**：异步增加送达重试次数，即TransactionLog中的asyncDeliveryTryTimes+1；Rdb实现就是update`transaction_log`表中`async_delivery_try_times`字段加1；Memory实现就是TransactionLog中重新给asyncDeliveryTryTimes赋值`new AtomicInteger(transactionLog.getAsyncDeliveryTryTimes()).incrementAndGet()`；
- **findEligibleTransactionLogs()**: 查询需要处理的事务日志，条件是：①`异步处理次数async_delivery_try_times小于参数最大处里次数maxDeliveryTryTimes`，②`transaction_type是BestEffortsDelivery`，③`系统当前时间与事务日志的创建时间差要超过参数maxDeliveryTryDelayMillis`，每次最多查询参数size条；Rdb实现通过sql从transaction_log表中查询，Memory实现遍历ConcurrentHashMap匹配符合条件的TransactionLog；
- **boolean processData()**：Rdb实现执行TransactionLog中的sql，如果执行过程中抛出异常，那么调用increaseAsyncDeliveryTryTimes()增加送达重试次数并抛出异常，如果执行成功，删除事务日志，并返回true；Memory实现直接返回false（因为processData()的目的是执行TransactionLog中的sql，而Memory类型无法触及数据库，所以返回false）

------

- 1.2事务日志RDB存储核心源码

事务日志RDB存储核心源码在`RdbTransactionLogStorage.java`中，主要提供了对事务日志表`transaction_log`的CRUD接口。

- 1.3事务日志存储样例

transaction_log中存储的事务日志样例：

![img](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHmYHq8W7PBN7yKCztjQIiah7XiaF6NhviaSibLQ2iabicpyBInFiclmGwWBHJnw2RN3WFuSb3orRmbse3XZw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

#### 1.2最大努力送达型事务监听器

核心源码如下：

```
@Slf4j
public final class BestEffortsDeliveryListener {

    @Subscribe
    @AllowConcurrentEvents
    // 从方法可知，只监听DML执行事件（DML即数据维护语言，包括INSERT, UPDATE, DELETE）
    public void listen(final DMLExecutionEvent event) {
        // 判断是否需要继续，判断逻辑为：事务存在，并且是BestEffortsDelivery类型事务
        if (!isProcessContinuously()) {
            return;
        }
        // 从柔性事务管理器中得到柔性事务配置
        SoftTransactionConfiguration transactionConfig = SoftTransactionManager.getCurrentTransactionConfiguration().get();
        // 得到配置的柔性事务存储器
        TransactionLogStorage transactionLogStorage = TransactionLogStorageFactory.createTransactionLogStorage(transactionConfig.buildTransactionLogDataSource());
        // 这里肯定是最大努力送达型事务（如果不是BEDSoftTransaction，isProcessContinuously()就是false）
        BEDSoftTransaction bedSoftTransaction = (BEDSoftTransaction) SoftTransactionManager.getCurrentTransaction().get();
        // 根据事件类型做不同处理
        switch (event.getEventExecutionType()) {
            case BEFORE_EXECUTE:
                // 如果执行前事件，那么先保存事务日志；
                //TODO for batch SQL need split to 2-level records
                transactionLogStorage.add(new TransactionLog(event.getId(), bedSoftTransaction.getTransactionId(), bedSoftTransaction.getTransactionType(), 
                        event.getDataSource(), event.getSql(), event.getParameters(), System.currentTimeMillis(), 0));
                return;
            case EXECUTE_SUCCESS: 
                // 如果执行成功事件，那么删除事务日志；
                transactionLogStorage.remove(event.getId());
                return;
            case EXECUTE_FAILURE: 
                boolean deliverySuccess = false;
                // 如果执行成功事件，最大努力送达型最多尝试3次（可配置，SoftTransactionConfiguration中的参数syncMaxDeliveryTryTimes）；
                for (int i = 0; i < transactionConfig.getSyncMaxDeliveryTryTimes(); i++) {
                    // 如果在该Listener中执行成功，那么返回，不需要再尝试
                    if (deliverySuccess) {
                        return;
                    }
                    boolean isNewConnection = false;
                    Connection conn = null;
                    PreparedStatement preparedStatement = null;
                    try {
                        conn = bedSoftTransaction.getConnection().getConnection(event.getDataSource(), SQLType.DML);
                        // 通过执行"select 1"判断conn是否是有效的数据库连接；如果不是有效的数据库连接，释放掉并重新获取一个数据库连接；
                        if (!isValidConnection(conn)) {
                            bedSoftTransaction.getConnection().release(conn);
                            conn = bedSoftTransaction.getConnection().getConnection(event.getDataSource(), SQLType.DML);
                            isNewConnection = true;
                        }
                        preparedStatement = conn.prepareStatement(event.getSql());
                        //TODO for batch event need split to 2-level records
                        for (int parameterIndex = 0; parameterIndex < event.getParameters().size(); parameterIndex++) {
                            preparedStatement.setObject(parameterIndex + 1, event.getParameters().get(parameterIndex));
                        }
                        // 因为只监控DML，所以调用executeUpdate()
                        preparedStatement.executeUpdate();
                        // executeUpdate()后能执行到这里，说明执行成功；根据id删除事务日志；
                        deliverySuccess = true;
                        transactionLogStorage.remove(event.getId());
                    } catch (final SQLException ex) {
                        // 如果sql执行有异常，那么输出error日志
                        log.error(String.format("Delivery times %s error, max try times is %s", i + 1, transactionConfig.getSyncMaxDeliveryTryTimes()), ex);
                    } finally {
                        close(isNewConnection, conn, preparedStatement);
                    }
                }
                return;
            default: 
                // 值支持三种事件类型，对于其他值，抛出异常
                throw new UnsupportedOperationException(event.getEventExecutionType().toString());
        }
    }

}
```

BestEffortsDeliveryListener源码总结：

- 执行前，插入事务日志；
- 执行成功，则删除事务日志；
- 执行失败，则最大努力尝试`syncMaxDeliveryTryTimes`次；

#### 1.3 异步送达JOB任务

同步重试若干次后，如果依然没有执行成功，可以通过部署的异步送达JOB任务继续重试，该特性将在下一篇文章详细讲解；

## 14. sjdbc之基于elastic-job的异步送达JOB

sharding-jdbc之异步送达JOB任务准备工作：

- 部署用于存储事务日志的数据库。
- 部署用于异步作业使用的zookeeper。
- 配置YAML文件，参照sharding-jdbc-transaction-async-job模块的示例文件config.yaml。
- 下载并解压文件sharding-jdbc-transaction-async-job-$VERSION.tar（也可以自己编译），通过start.sh脚本启动异步作业。

> 异步送达JOB任务基于elastic-job，所以需要部署zookeeper；

### 异步JOB任务源码结构

当最大努力送达型监听器多次失败尝试后，把任务交给最大努力送达型异步JOB任务处理，载异步尝试处理；核心源码在模块`sharding-jdbc-transaction-async-job`中。该模块是一个独立异步处理模块，使用者决定是否需要启用，源码比较少，大概看一下源码结构：

![异步JOB源码结构](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHmYHq8W7PBN7yKCztjQIiah7lfBy6G4BDd0mvKtZadDukaYiacnVmNGvRt13MpZsqxpfWQqUYCY9usA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)异步JOB源码结构

> resouces目录下的脚本和dubbo非常相似（我猜作者张亮应该也看过dubbo源码，哈），start.sh&stop.sh分别是服务启动脚本和服务停止脚本；根据start.sh脚本可知，该模块的主方法是**BestEffortsDeliveryJobMain.java**：

```
CONTAINER_MAIN=io.shardingjdbc.transaction.bed.BestEffortsDeliveryJobMain
nohup java -classpath $CONF_DIR:$LIB_DIR:. $CONTAINER_MAIN >/dev/null 2>&1 &
```



![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkbusjN22uB9gSv1RHY09FgQwhFZia1vSJ772J0ssxfXo0eLWHN4TSYYWzrHRFSk0eZO42WlIP1zrQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)



### 源码分析

这个BestEffortsDeliveryJobMain类，即异步JOB入口的核心源码如下：

```
public final class BestEffortsDeliveryJobMain {

    public static void main(final String[] args) throws Exception {
        try (InputStreamReader inputStreamReader = new InputStreamReader(BestEffortsDeliveryJobMain.class.getResourceAsStream("/conf/config.yaml"), "UTF-8")) {
            BestEffortsDeliveryConfiguration config = new Yaml(new Constructor(BestEffortsDeliveryConfiguration.class)).loadAs(inputStreamReader, BestEffortsDeliveryConfiguration.class);
            new BestEffortsDeliveryJobFactory(config).init();
        }
    }
}
```

由源码可知，这个模块的主配置文件是`config.yaml`；将该文件解析为**BestEffortsDeliveryConfiguration**，然后调用`new BestEffortsDeliveryJobFactory(config).init()`；

config.yaml配置文件中job相关配置内容如下：

```
jobConfig:
  #作业名称
  name: bestEffortsDeliveryJob
  #触发作业的cron表达式--每5s重试一次
  cron: 0/5 * * * * ?
  #每次JOB批量获取的事务日志最大数量
  transactionLogFetchDataCount: 100
  #事务送达的最大尝试次数.
  maxDeliveryTryTimes: 3
  #执行送达事务的延迟毫秒数,早于此间隔时间的入库事务才会被作业执行，其SQL为 where *** AND `creation_time`< (now() - maxDeliveryTryDelayMillis)，即至少60000ms，即一分钟前入库的事务日志才会被拉取出来；
  maxDeliveryTryDelayMillis: 60000
```

> `maxDeliveryTryDelayMillis: 60000`这个配置也可以理解为60s内的transaction_log不处理；

BestEffortsDeliveryJobFactory核心源码：

```
@RequiredArgsConstructor
public final class BestEffortsDeliveryJobFactory {

    // 这个属性赋值通过有参构造方法进行赋值--new BestEffortsDeliveryJobFactory(config)，就是通过`config.yaml`配置的属性
    private final BestEffortsDeliveryConfiguration bedConfig;

    /**
     * BestEffortsDeliveryJobMain中调用该init()方法，初始化最大努力尝试型异步JOB，该JOB基于elastic-job；
     * Initialize best efforts delivery job.
     */
    public void init() {
        // 根据config.yaml中配置的zkConfig节点，得到协调调度中心CoordinatorRegistryCenter
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(createZookeeperConfiguration(bedConfig));
        // 调度中心初始化
        regCenter.init();
        // 构造elastic-job调度任务
        JobScheduler jobScheduler = new JobScheduler(regCenter, createBedJobConfiguration(bedConfig));
        jobScheduler.setField("bedConfig", bedConfig);
        jobScheduler.setField("transactionLogStorage", TransactionLogStorageFactory.createTransactionLogStorage(new RdbTransactionLogDataSource(bedConfig.getDefaultTransactionLogDataSource())));
        // job调度任务初始化
        jobScheduler.init();
    }

    // 根据该方法可知，创建的是BestEffortsDeliveryJob
    private JobConfiguration createBedJobConfiguration(final BestEffortsDeliveryConfiguration bedJobConfig) {
        // 根据config.yaml中配置的jobConfig节点得到job配置信息，且指定job类型为BestEffortsDeliveryJob
        JobConfiguration result = new JobConfiguration(bedJobConfig.getJobConfig().getName(), BestEffortsDeliveryJob.class, 1, bedJobConfig.getJobConfig().getCron());
        result.setFetchDataCount(bedJobConfig.getJobConfig().getTransactionLogFetchDataCount());
        result.setOverwrite(true);
        return result;
    }
```

> 这一点代码跟elastic-job有一定关联。几个主要类：CoordinatorRegistryCenter，JobScheduler，JobConfiguration都是elastic-job的类。

BestEffortsDeliveryJob核心源码如下：
继承的类AbstractIndividualThroughputDataFlowElasticJob来自于elastic-job，并且两个方法fetchData()和processData()也来自于elastic-job：

- fetchDate()负责分批抓取事务日志（结果就是待处理的数据集合）；
- processData()会将事务日志进行分片处理（这个方法的一个参数shardingContext就表示JOB任务分片规则配置上下文）；

------

```
@Slf4j
public class BestEffortsDeliveryJob extends AbstractIndividualThroughputDataFlowElasticJob<TransactionLog> {

    @Override
    public List<TransactionLog> fetchData(final JobExecutionMultipleShardingContext context) {
        // 从transaction_log表中抓取最多100条事务日志（相关参数都在config.yaml中jobConfig节点下）
        return transactionLogStorage.findEligibleTransactionLogs(context.getFetchDataCount(), 
            bedConfig.getJobConfig().getMaxDeliveryTryTimes(), bedConfig.getJobConfig().getMaxDeliveryTryDelayMillis());
    }

    @Override
    public boolean processData(final JobExecutionMultipleShardingContext context, final TransactionLog data) {
        try (
            Connection conn = bedConfig.getTargetDataSource(data.getDataSource()).getConnection()) {
            // 调用事务日志存储器的processData()进行处理
            transactionLogStorage.processData(conn, data, bedConfig.getJobConfig().getMaxDeliveryTryTimes());
        } catch (final SQLException | TransactionCompensationException ex) {
            log.error(String.format("Async delivery times %s error, max try times is %s, exception is %s", data.getAsyncDeliveryTryTimes() + 1, 
                bedConfig.getJobConfig().getMaxDeliveryTryTimes(), ex.getMessage()));
            return false;
        }
        return true;
    }
}
```

> 说明，能够从transaction_log表中抓取到的事务日志，都是要重试的SQL。因为如果SQL执行成功的话，这条日志会被删除。

### 异步JOB总结

通过对异步JOB模块的分析可知，整个异步JOB是一个补偿机制，实现也比较简单：

- 初始化一个JOB任务；
- 批量拉取事务日志；
- 根据分片规则，对批量事务日志进行分片处理；



## 15. sharding-jdbc中的分布式ID

### 实现动机

传统数据库软件开发中，主键自动生成技术是基本需求。而各大数据库对于该需求也提供了相应的支持，比如MySQL的自增键。 对于MySQL而言，分库分表之后，不同表生成全局唯一的Id是非常棘手的问题。因为同一个逻辑表内的不同实际表之间的自增键是无法互相感知的， 这样会造成重复Id的生成。我们当然可以通过约束表生成键的规则来达到数据的不重复，但是这需要引入额外的运维力量来解决重复性问题，并使框架缺乏扩展性。

目前有许多第三方解决方案可以完美解决这个问题，比如UUID等依靠特定算法自生成不重复键（由于InnoDB采用的B+Tree索引特性，UUID生成的主键插入性能较差），或者通过引入Id生成服务等。 但也正因为这种多样性导致了Sharding-JDBC如果强依赖于任何一种方案就会限制其自身的发展。

基于以上的原因，最终采用了以JDBC接口来实现对于生成Id的访问，而将底层具体的Id生成实现分离出来。

> 摘自sharding-jdbc分布式主键：http://shardingjdbc.io/1.x/docs/02-guide/key-generator/

sharding-jdbc的分布式ID采用twitter开源的snowflake算法，不需要依赖任何第三方组件，这样其扩展性和维护性得到最大的简化。但是snowflake算法最大的缺陷，即强依赖时间，如果时钟回拨，就会生成重复的ID，sharding-jdbc没有给出解决方案，如果用户想要解决这个问题，需要自行优化；

> 美团的分布式ID生成系统也是基于snowflake算法，并且解决了时钟回拨的问题，读者有兴趣请阅读Leaf——美团点评分布式ID生成系统：https://tech.meituan.com/MT_Leaf.html

### 分布式ID简介

github上对分布式ID这个特性的描述是：`Distributed Unique Time-Sequence Generation`，两个重要特性是：**分布式唯一**和**时间序**；基于Twitter Snowflake算法实现，长度为64bit；64bit组成如下：

- 1bit ：签名校验位，也可以当做预留位。
- 41bits ：当前时间与服务第一版本发布时间的差值。
- 10bits ：进程ID（也可以分解成2bit+8bit，其中2bit表示数据中心，可以容纳4个数据中心，8bit表示进程ID，可以256个进程）。
- 12bits ：每一毫秒的自增值。所以snowflake算法能达到4096/ms的并发能力。

------

![snowflake算法核心图](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHnia8J5wibBmSEicarkFpD4byNhJSlnV87dFa4uiboStNggVku73LLUeU5ZdkFIDcZnNZGxaamUicCZicFQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)snowflake算法核心图

### 分布式ID源码分析

核心源码在**sharding-jdbc-core**模块中的`io.shardingjdbc.core.keygen.DefaultKeyGenerator.java`中：

```
public final class DefaultKeyGenerator implements KeyGenerator {

    public static final long EPOCH;    
    // 自增长序列的长度（单位是位时的长度）
    private static final long SEQUENCE_BITS = 12L;
    // workerId的长度（单位是位时的长度）
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;
    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;
    // 位运算计算workerId的最大值（workerId占10位，那么1向左移10位就是workerId的最大值）
    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;    
    @Setter
    private static TimeService timeService = new TimeService();
    // 工作机器ID
    private static long workerId;

    // EPOCH就是起始时间，从2016-11-01 00:00:00开始的毫秒数，这是sharding-jdbc第一个版本的发布时间
    static {
        // 省略逻辑代码
        EPOCH = "2016-11-01 00:00:00".getTimeInMillis();
    }

    // 每一毫秒的自增序列数
    private long sequence;    
    // 上一次取分布式ID的时间
    private long lastTime;

    // 得到分布式唯一ID需要先设置workerId，workId的值限定范围[0, 1024)
    public static void setWorkerId(final long workerId) {
        // google-guava提供的入参检查方法：workerId只能在0~WORKER_ID_MAX_VALUE之间；
        Preconditions.checkArgument(workerId >= 0L && workerId < WORKER_ID_MAX_VALUE);
        DefaultKeyGenerator.workerId = workerId;
    }

    // 调用该方法，得到分布式唯一ID
    @Override
    public synchronized Number generateKey() {
        long currentMillis = timeService.getCurrentMillis();
        // 每次取分布式唯一ID的时间不能少于上一次取时的时间
        Preconditions.checkState(lastTime <= currentMillis, "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", lastTime, currentMillis);
        // 如果同一毫秒范围内，那么自增，否则从0开始
        if (lastTime == currentMillis) {
            // 如果自增后的sequence值超过4096，那么等待直到下一个毫秒
            if (0L == (sequence = ++sequence & SEQUENCE_MASK)) {
                currentMillis = waitUntilNextTime(currentMillis);
            }
        } else {
            sequence = 0;
        }
        // 更新lastTime的值，即最后一次获取分布式唯一ID的时间
        lastTime = currentMillis;
        // 从这里可知分布式唯一ID的组成部分；
        return ((currentMillis - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }

    // 获取下一毫秒的方法：死循环获取当前毫秒与lastTime比较，直到大于lastTime的值；
    private long waitUntilNextTime(final long lastTime) {
        long time = timeService.getCurrentMillis();
        // 毕竟最长死循环1ms，而且还要在1ms内产生了4096个分布式唯一ID，所以这个while循环不能造成CPU飙高的问题。
        while (time <= lastTime) {
            time = timeService.getCurrentMillis();
        }
        return time;
    }
}
```

### 获取workerId的三种方式

sharding-jdbc的`sharding-jdbc-plugin`模块中，提供了三种方式获取workerId的方式，并提供接口获取分布式唯一ID的方法--`generateKey()`，接下来对各种方式如何生成workerId进行分析；

## HostNameKeyGenerator

1. 根据hostname获取，源码如下（HostNameKeyGenerator.java）：

```
/**
 * 根据机器名最后的数字编号获取工作进程Id.如果线上机器命名有统一规范,建议使用此种方式.
 * 例如机器的HostName为:dangdang-db-sharding-dev-01(公司名-部门名-服务名-环境名-编号)
 * ,会截取HostName最后的编号01作为workerId.
 *
 * @author DonneyYoung
 **/
 static void initWorkerId() {
    InetAddress address;
    Long workerId;
    try {
        address = InetAddress.getLocalHost();
    } catch (final UnknownHostException e) {
        throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
    }
    // 先得到服务器的hostname，例如JTCRTVDRA44，linux上可通过命令"cat /proc/sys/kernel/hostname"查看；
    String hostName = address.getHostName();
    try {
        // 计算workerId的方式：
        // 第一步hostName.replaceAll("\\d+$", "")，即去掉hostname后纯数字部分，例如JTCRTVDRA44去掉后就是JTCRTVDRA
        // 第二步hostName.replace(第一步的结果, "")，即将原hostname的非数字部分去掉，得到纯数字部分，就是workerId
        workerId = Long.valueOf(hostName.replace(hostName.replaceAll("\\d+$", ""), ""));
    } catch (final NumberFormatException e) {
        // 如果根据hostname截取不到数字，那么抛出异常
        throw new IllegalArgumentException(String.format("Wrong hostname:%s, hostname must be end with number!", hostName));
    }
    DefaultKeyGenerator.setWorkerId(workerId);
}
```

#### IPKeyGenerator

1. 根据IP获取，源码如下（IPKeyGenerator.java）：

```
/**
 * 根据机器IP获取工作进程Id,如果线上机器的IP二进制表示的最后10位不重复,建议使用此种方式
 * ,列如机器的IP为192.168.1.108,二进制表示:11000000 10101000 00000001 01101100
 * ,截取最后10位 01 01101100,转为十进制364,设置workerId为364.
 */
static void initWorkerId() {
    InetAddress address;
    try {
        // 首先得到IP地址，例如192.168.1.108
        address = InetAddress.getLocalHost();
    } catch (final UnknownHostException e) {
        throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
    }
    // IP地址byte[]数组形式，这个byte数组的长度是4，数组0~3下标对应的值分别是192，168，1，108
    byte[] ipAddressByteArray = address.getAddress();
    // 由这里计算workerId源码可知，workId由两部分组成：
    // 第一部分(ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE：ipAddressByteArray[ipAddressByteArray.length - 2]即取byte[]倒数第二个值，即1，然后&0B11，即只取最后2位（IP段倒数第二个段取2位，IP段最后一位取全部8位，总计10位），然后左移Byte.SIZE，即左移8位（因为这一部分取得的是IP段中倒数第二个段的值）；
    // 第二部分(ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF)：ipAddressByteArray[ipAddressByteArray.length - 1]即取byte[]最后一位，即108，然后&0xFF，即通过位运算将byte转为int；
    // 最后将第一部分得到的值加上第二部分得到的值就是最终的workId
    DefaultKeyGenerator.setWorkerId((long) (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF)));
}
```

#### IPSectionKeyGenerator

1. 根据IP段获取，源码如下（IPSectionKeyGenerator.java）：

```
/**
 * 浏览 {@link IPKeyGenerator} workerId生成的规则后，感觉对服务器IP后10位（特别是IPV6）数值比较约束.
 * 
 * <p>
 * 有以下优化思路：
 * 因为workerId最大限制是2^10，我们生成的workerId只要满足小于最大workerId即可。
 * 1.针对IPV4:
 * ....IP最大 255.255.255.255。而（255+255+255+255) < 1024。
 * ....因此采用IP段数值相加即可生成唯一的workerId，不受IP位限制。
 * 2.针对IPV6:
 * ....IP最大ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 * ....为了保证相加生成出的workerId < 1024,思路是将每个bit位的后6位相加。这样在一定程度上也可以满足workerId不重复的问题。
 * </p>
 * 使用这种IP生成workerId的方法,必须保证IP段相加不能重复
 *
 * @author DogFc
 */
static void initWorkerId() {
    InetAddress address;
    try {
        address = InetAddress.getLocalHost();
    } catch (final UnknownHostException e) {
        throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
    }
    // 得到IP地址的byte[]形式值
    byte[] ipAddressByteArray = address.getAddress();
    long workerId = 0L;
    //如果是IPV4，计算方式是遍历byte[]，然后把每个IP段数值相加得到的结果就是workerId
    if (ipAddressByteArray.length == 4) {
        for (byte byteNum : ipAddressByteArray) {
            workerId += byteNum & 0xFF;
        }
        //如果是IPV6，计算方式是遍历byte[]，然后把每个IP段后6位（& 0B111111 就是得到后6位）数值相加得到的结果就是workerId
    } else if (ipAddressByteArray.length == 16) {
        for (byte byteNum : ipAddressByteArray) {
            workerId += byteNum & 0B111111;
        }
    } else {
        throw new IllegalStateException("Bad LocalHost InetAddress, please check your network!");
    }
    DefaultKeyGenerator.setWorkerId(workerId);
}
```

### 总结

**大道至简**，笔者还是比较推荐**HostNameKeyGenerator**方式获取workerId，只需服务器按照标准统一配置好hostname即可；这种方案有点类似spring-boot：**约定至上**；并能够让架构最简化，不依赖任何第三方组件。因为依赖的组件越多，维护成本越高，出问题概率越大，可靠性更低！

当然HostNameKeyGenerator这种方式的话，就不能一台服务器上部署多个实例，事实上这不是问题。一般生产环境会有很多的服务器，也有很多的服务实例。我们可以通过交叉部署来避免这个问题。比如不要一台服务器上部署3个订单服务，而是3台服务器上分别部署1个订单服务实例，服务器上多余的资源可以部署其他服务。

总之，每种方法都有它的优缺点，我们要做的就是trade-off。

## 16. sjdbc之读写分离

### 读写分离支持范围

- 提供了一主多从的读写分离配置，可独立使用，也可配合分库分表使用。
- 同一线程且同一数据库连接内，如有写入操作，以后的读操作均从主库读取，用于保证数据一致性。
- Spring命名空间。
- 基于Hint的强制主库路由。

### 读写分离不支持范围

- 主库和从库的数据同步
- 主库和从库的数据同步延迟导致的数据不一致。
- 主库双写或多写。

------

> 读写分离支持项和不支持范围摘自sharding-jdbc使用指南☞读写分离：http://shardingjdbc.io/docs_1.x/02-guide/master-slave/

### 简单总结

sharding-jdbc的读写分离不甘于数据库层面的东西。比如主从同步，依赖mysql本身的同步机制，而不是由sharding-jdbc去支持。而主从同步延迟这种问题，sharding-jdbc也没必要去解决。

### 源码分析

#### MasterSlave or Sharding

选择MasterSlaveDataSource还是普通的ShardingDataSource逻辑非常简单，就看根据sharding配置能否得到slave，核心源码如下：

```
// 数据源名称与数据库连接关系缓存，例如：{dbtbl_0_master:Connection实例; dbtbl_1_master:Connection实例; dbtbl_0_slave_0:Connection实例; dbtbl_0_slave_1:Connection实例; dbtbl_1_slave_0:Connection实例; dbtbl_1_slave_1:Connection实例}
private final Map<String, Connection> cachedConnections = new HashMap<>();

/**
 * 根据数据源名称得到数据库连接
 */
public Connection getConnection(final String dataSourceName, final SQLType sqlType) throws SQLException {
    // 首先尝试从local cache（map类型）中获取，如果已经本地缓存，那么直接从本地缓存中获取
    if (getCachedConnections().containsKey(dataSourceName)) {
        return getCachedConnections().get(dataSourceName);
    }
    DataSource dataSource = shardingContext.getShardingRule().getDataSourceRule().getDataSource(dataSourceName);
    Preconditions.checkState(null != dataSource, "Missing the rule of %s in DataSourceRule", dataSourceName);
    String realDataSourceName;
    // 如果是主从数据库的话（例如xml中配置<rdb:master-slave-data-source id="dbtbl_0" ...>，那么dbtbl_0就是主从数据源）
    if (dataSource instanceof MasterSlaveDataSource) {
        // 见后面的"主从数据源中根据负载均衡策略获取数据源"的分析
        NamedDataSource namedDataSource = ((MasterSlaveDataSource) dataSource).getDataSource(sqlType);
        realDataSourceName = namedDataSource.getName();
        // 如果主从数据库元选出的数据源名称（例如：dbtbl_1_slave_0）与数据库连接已经被缓存，那么从缓存中取出数据库连接
        if (getCachedConnections().containsKey(realDataSourceName)) {
            return getCachedConnections().get(realDataSourceName);
        }
        dataSource = namedDataSource.getDataSource();
    } else {
        realDataSourceName = dataSourceName;
    }
    Connection result = dataSource.getConnection();
    // 把数据源名称与数据库连接实例缓存起来
    getCachedConnections().put(realDataSourceName, result);
    replayMethodsInvocation(result);
    return result;
}
```

#### 强制主&负载均衡选择

主从数据源中根据负载均衡策略获取数据源核心源码--MasterSlaveDataSource.java：

```
// 主数据源, 例如dbtbl_0_master对应的数据源
@Getter
private final DataSource masterDataSource;

// 主数据源下所有的从数据源，例如{dbtbl_0_slave_0:DataSource实例; dbtbl_0_slave_1:DataSource实例}
@Getter
private final Map<String, DataSource> slaveDataSources;

public NamedDataSource getDataSource(final SQLType sqlType) {
    if (isMasterRoute(sqlType)) {
        DML_FLAG.set(true);
        // 如果符合主路由规则，那么直接返回主路由（不需要根据负载均衡策略选择数据源）
        return new NamedDataSource(masterDataSourceName, masterDataSource);
    }
    // 负载均衡策略选择数据源名称[后面会分析]
    String selectedSourceName = masterSlaveLoadBalanceStrategy.getDataSource(name, masterDataSourceName, new ArrayList<>(slaveDataSources.keySet()));
    DataSource selectedSource = selectedSourceName.equals(masterDataSourceName) ? masterDataSource : slaveDataSources.get(selectedSourceName);
    Preconditions.checkNotNull(selectedSource, "");
    return new NamedDataSource(selectedSourceName, selectedSource);
}

// 主路由逻辑
private boolean isMasterRoute(final SQLType sqlType) {
    return SQLType.DQL != sqlType || DML_FLAG.get() || HintManagerHolder.isMasterRouteOnly();
}
```

主路由逻辑如下：

1. 非查询SQL（SQLType.DQL != sqlType）
2. 当前数据源在当前线程访问过主库（数据源访问过主库就会通过ThreadLocal将DML_FLAG置为true，从而路由主库）（DML_FLAG.get()）
3. HintManagerHolder方式设置了主路由规则（HintManagerHolder.isMasterRouteOnly()）

------

> 当前线程访问过主库后，后面的操作全部切主，是为了防止主从同步数据延迟导致写操作后，读不到最新的数据？我想应该是这样的^^

### 主从负载均衡分析

从对`MasterSlaveDataSource.java`的分析可知，如果不符合强制主路由规则，那么会根据负载均衡策略选多个slave中选取一个slave，**MasterSlaveLoadBalanceAlgorithm**接口有两个实现类：

- **RoundRobinMasterSlaveLoadBalanceAlgorithm**即轮训策略；
- **RandomMasterSlaveLoadBalanceAlgorithm**随机策略；

笔者有话要说：这里没有出镜率比较高的一致性hash策略，这倒是个意外，哈！

------

#### 轮询策略

轮询方式的实现类为**RoundRobinMasterSlaveLoadBalanceStrategy**，核心源码如下：

```
public final class RoundRobinMasterSlaveLoadBalanceStrategy implements MasterSlaveLoadBalanceStrategy {

    private static final ConcurrentHashMap<String, AtomicInteger> COUNT_MAP = new ConcurrentHashMap<>();

    @Override
    public String getDataSource(final String name, final String masterDataSourceName, final List<String> slaveDataSourceNames) {
        // 每个集群体系都有自己的计数器，例如dbtbl_0集群，dbtbl_1集群；如果COUNT_MAP中还没有这个集群体系，需要先初始化；
        AtomicInteger count = COUNT_MAP.containsKey(name) ? COUNT_MAP.get(name) : new AtomicInteger(0);
        COUNT_MAP.putIfAbsent(name, count);
        // 如果轮询计数器（AtomicInteger count）长到slave.size()，那么归零（防止计数器不断增长下去）
        count.compareAndSet(slaveDataSourceNames.size(), 0);
        // 计数器递增，根据计算器的值对slave集群数值取模就是从slave集合中选中的目标slave的下标
        return slaveDataSourceNames.get(count.getAndIncrement() % slaveDataSourceNames.size());
    }
}
```

------

注意轮询选择最后一行代码：

```
return slaveDataSourceNames.get(count.getAndIncrement() % slaveDataSourceNames.size())
```

count.getAndIncrement()然后取模，而count的初始值为0。这就有一个非常小的隐患，当轮询策略选择了Integer.MAX_VALUE次后，再count.getAndIncrement()就会变成负数，从而导致执行List集合的get()方法出错。验证这个潜在问题的代码如下：

```
public class SlaveDataSourceSelectDemo {

    public static void main(String[] args) {

        List<String> dataSources = new ArrayList<>();
        dataSources.add("datasource-1");
        dataSources.add("datasource-2");
        dataSources.add("datasource-3");

        AtomicInteger count = new AtomicInteger(Integer.MAX_VALUE-3);
        for (int i = 0; i < 5; i++) {
            System.out.println("selected: "+dataSources.get(count.getAndIncrement() % dataSources.size()));
        }
    }
}
```

模拟主从总计3个节点，当轮询Integer.MAX_VALUE-3后，再尝试轮询5次，在第4次就会报错！

笔者在github上找到了sharding-sphere最新的代码，这个问题依然存在，明天找张亮大神确认一下，最新源码路径为：
https://github.com/sharding-sphere/sharding-sphere/blob/dev/sharding-core/src/main/java/io/shardingsphere/core/api/algorithm/masterslave/RoundRobinMasterSlaveLoadBalanceAlgorithm.java

#### 随机策略

随机方式的实现类为**RandomMasterSlaveLoadBalanceAlgorithm**，核心源码如下：

```
public final class RandomMasterSlaveLoadBalanceAlgorithm implements MasterSlaveLoadBalanceAlgorithm {    
    @Override
    public String getDataSource(final String name, final String masterDataSourceName, final List<String> slaveDataSourceNames) {
        // 从实例范围取一个随机数（例如总计有3个slave实例，那么new Random().nextInt(3)），就是从slave集合中选中的目标slave的下标
        return slaveDataSourceNames.get(new Random().nextInt(slaveDataSourceNames.size()));
    }
}
```

#### 默认策略

```java
public enum MasterSlaveLoadBalanceAlgorithmType {

    ROUND_ROBIN(new RoundRobinMasterSlaveLoadBalanceAlgorithm()),
    RANDOM(new RandomMasterSlaveLoadBalanceAlgorithm());

    private final MasterSlaveLoadBalanceAlgorithm algorithm;

    // 默认策略就是轮询策略
    public static MasterSlaveLoadBalanceAlgorithmType getDefaultAlgorithmType() {
        return ROUND_ROBIN;
    }
}
```

## 17. orchestration简介&使用

### orchestration介绍

sharding-jdbc2.x核心功能之一就是orchestration，即编排治理。sharding-jdbc从2.0.0.M1版本开始，提供了数据库治理功能，主要包括：

- 配置集中化与动态化。可支持数据源、表与分片及读写分离策略的**动态切换**；
- 数据治理。提供熔断数据库访问程序对数据库的访问和禁用从库的访问的能力；
- 支持Zookeeper和etcd的注册中心；

> 摘自sharding-jdbc编排治理：http://shardingsphere.io/document/legacy/2.x/cn/02-guide/orchestration/，官方文档也有比较详细的使用说明；

### 架构图

![sharding-jdbc2.0.3架构图](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHl9nLExrT7nicxe7l9HPRCAial7C87AY2xG4neEBZJSCZvOEXgkDJbJiccaIuwAOCoIwWoapBQjVNz5A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)sharding-jdbc2.0.3架构图

由sharding-jdbc2.x新的架构图可知，与sharding-jdbc1.x版本相比，最大的变化就是最左边的sharding-jdbc-orchestration。即为了动态修改配置引入的注册中心和编排模块。而sharding-jdbc核心架构几乎没有任何改变。



#### 注册中心数据结构

注册中心在定义的命名空间下，创建数据库访问对象运行节点，用于区分不同数据库访问实例。命名空间中包含2个数据子节点，分别是config和state。

##### config节点

config节点信息如下：

```
config
    ├──datasource                                数据源（可能多个，数据结构为json数组）配置
    ├──sharding                                  分库分表（包括分库分表+读写分离）配置根节点
    ├      ├──rule                               分库分表（包括分库分表+读写分离）规则
    ├      ├──configmap                          分库分表ConfigMap配置，以K/V形式存储，如：{"k1":"v1"}
    ├      ├──props                              Properties配置
    ├──masterslave                               读写分离独立使用配置
    ├      ├──rule                               读写分离规则
    ├      ├──configmap                          读写分离ConfigMap配置，以K/V形式存储，如：{"k1":"v1"}
```

##### state节点

state节点包括instances和datasource节点。
instances节点信息如下：

```
instances
    ├──your_instance_ip_a@-@your_instance_pid_x
    ├──your_instance_ip_b@-@your_instance_pid_y
    ├──....    
```

### 简单使用

接下来讲解如何在ssm（spring、springmvc、mybatis）结构的程序上集成sharding-jdbc（版本为**2.0.3**）进行分库分表，并集成sharding-jdbc2.x最新特性orchestration；
假设分库分表行为如下：

- 将auth_user表分到4个库（user_0~user_3）中；
- 其他表不进行分库分表，保留在default_db库中；
- 集成orchestration特性，即编排治理，可动态维护配置信息；

#### 1.POM配置

以spring配置文件为例，新增如下POM配置：

```
<dependency>
    <groupId>io.shardingjdbc</groupId>
    <artifactId>sharding-jdbc-core</artifactId>
    <version>2.0.3</version>
</dependency>

<!--orchestration特性需要引入下面这个maven坐标-->
<dependency>
    <groupId>io.shardingjdbc</groupId>
    <artifactId>sharding-jdbc-orchestration</artifactId>
    <version>${io-shardingjdbc.version}</version>
</dependency>

<!--如果通过spring配置集成orchestration特性, 需要增加如下maven坐标-->
<dependency>
    <groupId>io.shardingjdbc</groupId>
    <artifactId>sharding-jdbc-orchestration-spring-namespace</artifactId>
    <version>${io-shardingjdbc.version}</version>
</dependency>
```

- 说明

由于引入了maven坐标：sharding-jdbc-orchestration-spring-namespace，所以一定不要同时引入另一个maven坐标：sharding-jdbc-core-spring-namespace。因为两个模块对应的namespace不同，而且两个模块中**spring.handlers**定义的NamespaceHandler也不一样。如果同时配置了这两个maven坐标，会导致加载出错抛出下面的异常信息：

```
[spring.xml] is invalid; nested exception is org.xml.sax.SAXParseException; systemId: http://shardingjdbc.io/schema/shardingjdbc/orchestration/sharding/sharding.xsd; lineNumber: 7; columnNumber: 48; TargetNamespace.1: Expecting namespace 'http://shardingjdbc.io/schema/shardingjdbc/orchestration/sharding', but the target namespace of the schema document is 'http://shardingjdbc.io/schema/shardingjdbc/sharding'.
```

#### 2.配置数据源

spring-datasource.xml和  中保持一致；

#### 3.集成sharding数据源

spring-sharding.xml配置如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <!--使用orchestration特性需要增加的注册中心配置-->
    <reg:zookeeper id="regCenter" server-lists="localhost:2181"
                   namespace="orchestration-spring-namespace-test"
                   base-sleep-time-milliseconds="1000"
                   max-sleep-time-milliseconds="3000"
                   max-retries="3" />

    <!--数据库sharding策略-->
    <sharding:standard-strategy id="databaseStrategy" sharding-column="id"
                                precise-algorithm-class="com.crt.fin.ospsso.service.shardingjdbc.AuthUserDatabaseShardingAlgorithm" />
    <!--auth_user表sharding策略:无 -->

    <sharding:none-strategy id="noneStrategy" />

    <sharding:data-source id="shardingDataSource" overwrite="true" registry-center-ref="regCenter" >
        <!--default-data-source指定默认数据源, 即没有在<rdb:table-rules>申明的logic-table表,
        即不需要分库分表的表, 全部走默认数据源-->
        <sharding:sharding-rule data-source-names="sj_ds_0,sj_ds_1,sj_ds_2,sj_ds_3,sj_ds_default"
                                default-data-source-name="sj_ds_default"
                                default-database-strategy-ref="noneStrategy"
                                default-table-strategy-ref="noneStrategy">
            <sharding:table-rules>
                <!--auth_user只分库不分表, actual-tables的值一定要加上:sj_ds_${0..3}.,
                否则会遍历data-sources, 而sj_ds_default中并没有auth_user表 -->
                <sharding:table-rule logic-table="auth_user" actual-data-nodes="sj_ds_${0..3}.auth_user"

                                database-strategy-ref="databaseStrategy"/>
            </sharding:table-rules>
        </sharding:sharding-rule>
        <sharding:props>
            <prop key="sql.show">false</prop>
            <prop key="executor.size">2</prop>
        </sharding:props>
    </sharding:data-source>

    <!-- 配置sqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!---datasource交给sharding-jdbc托管-->
        <property name="dataSource" ref="shardingDataSource"/>
        <property name="mapperLocations" value="classpath*:mybatis/*Mapper.xml"/>
    </bean>

    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.crt.fin.ospsso.dal.mapper"/>
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    </bean>

</beans>
```

说明：这些的代码和  非常类似，但是有几处重要的不同点：

1. **namespace**的变更（对应的sharding的命名空间由：**http://shardingjdbc.io/schema/shardingjdbc/sharding**，变更为：**http://shardingjdbc.io/schema/shardingjdbc/orchestration/sharding**）；
2. ``中新增`registry-center-ref="regCenter"`，并新增``即配置中心节点配置；

#### 4.Main测试

Main.java用来测试分库分表是否OK，其源码如下：

```
/**
 * @author afei
 * @version 1.0.0
 * @since 2018年09月14日
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "/META-INF/spring/spring-*.xml");

        // auth_user有进行分库，
        AuthUserMapper authUserMapper = context.getBean(AuthUserMapper.class);
        AuthUser authUser = authUserMapper.selectByPrimaryKey(7L);
        System.out.println("-----> The auth user: "+JSON.toJSONString(authUser));

        System.out.println("sleeping...."+new Date());
        // 留点时间以便通过zkClient执行set命令
        Thread.sleep(15000);

        AuthUserMapper authUserMapper2 = context.getBean(AuthUserMapper.class);
        AuthUser authUser2 = authUserMapper2.selectByPrimaryKey(7L);
        System.out.println("-----> The auth user: "+JSON.toJSONString(authUser2));
    }

}
```

说明：
在执行第二条SQL之前，sleep一段时间，为了留出时间通过zkClient执行set命令动态更新配置信息，执行的set命令如下：

```
set /orchestration-spring-namespace-test/shardingDataSource/config/sharding/props {"executor.size":"2","sql.show":"true"}
```

------

- 验证日志

由于xml文件中初始配置`<prop key="sql.show">false</prop>`，所以执行的第一条SQL不会输出逻辑SQL和实际SQL信息；然后通过set命令动态更新配置后，执行第二条SQL时会输出逻辑SQL和实际SQL信息；

- 重启问题

上面的修改只会影响zookeeper即配置中心里的配置，而程序里的配置并没有变更，如果重启服务的话，配置又会退回去，这个问题怎么办？一般服务都会集成分布式配置管理平台例如disconf，apollo等。这样的话，把**spring-sharding.xml**以及其他xml文件中的具体配置抽离到一个properties文件中。当我们通过set命令更新配置中心里的配置的同时，也同步修改分布式配置管理平台上维护的配置，这样的话，即使重启也会加载到最新的配置。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHkbusjN22uB9gSv1RHY09FgdS44XeeI9rq2z3D7NibNqyXKH2uuicjgIcDAt6CIslO7nyscyanGgyBA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

### 总结

如果熟悉dubbo服务的注册&发现机制的话，就很容易理解sharding-jdbc的编排治理。服务治理原理都是大同小异：将配置信息持久化并注册监听，如果配置信息改变，通过监听机制可动态改变适应新配置。从而达到不需要重启服务的目的；sharding-jdbc的编排治理核心步骤如下所示：

1. sharding-jdbc启动时，将相关配置信息以JSON格式存储，包括数据源，分库分表，读写分离、ConfigMap及Properties配置等信息持久化到注册中心节点上；
2. 注册节点的监听。
3. 当节点信息发生变化，sharding-jdbc将刷新配置信息；

> 下一篇文章会基于源码分析sharding-jdbc的编排治理实现原理；

### 不足

遗憾的是，sharding-jdbc2.x没有提供可视化操作途径。以zookeeper作为配置中心为例，用户需要自己登陆zkClient，并通过set命令修改某节点对应的值；例如在zkClient中执行如下命令通过设置sql.show为true，从而开启输出SQL日志功能：

```
set /orchestration-yaml-test/demo_ds_ms/config/sharding/props {"sql.show":false}
```

这种操作还是比较危险，需要非常谨慎。期待未来sharding-jdbc能够提供UI操作界面。当年，sharding-jdbc已经实现到这一步，我们自己也可以开发一个UI操作界面，这已经不是很难的事情。

### 附:zk监听机制

Zookeeper官方对其监听机制描述如下（如果选择etcd为注册中心的话，请自己去了解相关特性）：
ZooKeeper supports the concept of watches. **Clients can set a watch on a znodes**. **A watch will be triggered and removed when the znode changes**. When a watch is triggered the client receives a packet saying that the znode has changed.

## 18. orchestration实现原理

### 源码图解

![orchestration源码结构图.png](https://mmbiz.qpic.cn/mmbiz_png/4o22OFcmzHl9nLExrT7nicxe7l9HPRCAiamWibaXv1P2QaDvES62Mt5lMTdPWfv03JzL2Brd8ohLGps13g33jia3PQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)orchestration源码结构图.png

根据源码图解可知，**sharding-jdbc-orchestration**模块中创建数据源有两种方式：工厂类和spring；且有两种数据源类型：**OrchestrationShardingDataSource**和**OrchestrationMasterSlaveDataSource**；

- 左边是**OrchestrationShardingDataSource**类型数据源创建，配置信息持久化以及监听&刷新过程；右边是**OrchestrationMasterSlaveDataSource**类型数据源创建，配置信息持久化以及监听&刷新过程；
- 工厂类方式通过**OrchestrationShardingDataSourceFactory**或者**OrchestrationMasterSlaveDataSourceFactory**创建；
- spring方式通过解析xml配置文件创建（可以参考**OrchestrationShardingNamespaceTest**测试用例）；
- 得到数据源后，调用OrchestrationFacade.init()方法；在该init()方法中持久化配置信息到注册中心中；并创建监听器；

> 由图可知，两种类型数据源的处理大同小异，本篇文章只分析**OrchestrationShardingDataSource**这种类型的数据源；

### 源码分析

接下来通过工厂类创建**OrchestrationShardingDataSource**类型数据源源码剖析orchestration的实现原理；

![img](https://mmbiz.qpic.cn/mmbiz_jpg/4o22OFcmzHlZd9Hzho814J6rxcicZW1C0fl3xQHh4FTkA4m6wChsd6xUL5fB1VPDQ7hLOOPhNbfgvzwYR95FDsA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

#### 1.创建数据源

通过测试用例**YamlOrchestrationShardingIntegrateTest**可知，创建数据源的代码为**OrchestrationShardingDataSourceFactory.createDataSource(yamlFile);**这段代码的实现如下所示：

```
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrchestrationShardingDataSourceFactory {

    public static DataSource createDataSource(
            final Map<String, DataSource> dataSourceMap, final ShardingRuleConfiguration shardingRuleConfig, 
            final Map<String, Object> configMap, final Properties props, 
            final OrchestrationConfiguration orchestrationConfig) throws SQLException {
        // step3.1 创建OrchestrationShardingDataSource数据源
        OrchestrationShardingDataSource result = new OrchestrationShardingDataSource(dataSourceMap, shardingRuleConfig, configMap, props, orchestrationConfig);
        // step3.2 初始化（这里是sharding-jdb orchestration编排治理的核心）
        result.init();
        return result;
    }

    public static DataSource createDataSource(final File yamlFile) throws SQLException, IOException {
        // step1. 解析yaml文件得到YamlOrchestrationShardingRuleConfiguration
        YamlOrchestrationShardingRuleConfiguration config = unmarshal(yamlFile);
        // step2. 得到分库分表规则配置，即根据yaml文件中shardingRule节点信息得到的分库分表规则配置
        YamlShardingRuleConfiguration shardingRuleConfig = config.getShardingRule();
        // step3. 调用上面的方法创建数据源
        return createDataSource(config.getDataSources(), shardingRuleConfig.getShardingRuleConfiguration(),  
                shardingRuleConfig.getConfigMap(), shardingRuleConfig.getProps(), config.getOrchestration().getOrchestrationConfiguration());
    }

    // 一些其他创建数据源的方式，大同小异，暂时省略
    ... ...
}
```

> OrchestrationShardingDataSource.init()方法会调用OrchestrationFacade.init()方法，所以分析后者即可；

#### 2.持久化

OrchestrationFacade.init()核心源码如下：

```
public void init(
        final Map<String, DataSource> dataSourceMap, 
        final ShardingRuleConfiguration shardingRuleConfig, 
        final Map<String, Object> configMap, 
        final Properties props, 
        final ShardingDataSource shardingDataSource) throws SQLException {
    // step1. 持久化sharding规则配置，且为PERSISTENT类型节点
    configService.persistShardingConfiguration(getActualDataSourceMapForMasterSlave(dataSourceMap), shardingRuleConfig, configMap, props, isOverwrite);
    // step2. 持久化sharding实例信息，且为EPHEMERAL类型节点
    instanceStateService.persistShardingInstanceOnline();
    // step3. 持久化数据源节点信息，且为PERSISTENT类型节点
    dataSourceService.persistDataSourcesNode();
    // step4. 注册监听器
    listenerManager.initShardingListeners(shardingDataSource);
}
```

> 所以说，这里就是sharding-jdbc编排治理的核心--配置信息持久化，注册监听器；接下来先分析编排治理的配置信息持久化；

#### 2.1持久化sharding规则配置

持久化sharding规则配置的核心实现如下，我们接下来一一分析其持久化的内容；

```
public void persistShardingConfiguration(
        final Map<String, DataSource> dataSourceMap, 
        final ShardingRuleConfiguration shardingRuleConfig, 
        final Map<String, Object> configMap, 
        final Properties props, final boolean isOverwrite) {
    persistDataSourceConfiguration(dataSourceMap, isOverwrite);
    persistShardingRuleConfiguration(shardingRuleConfig, isOverwrite);
    persistShardingConfigMap(configMap, isOverwrite);
    persistShardingProperties(props, isOverwrite);
}
```

- 持久化数据源配置

对应源码为**persistDataSourceConfiguration(dataSourceMap, isOverwrite);**核心实现源码如下：

```
private void persistDataSourceConfiguration(final Map<String, DataSource> dataSourceMap, final boolean isOverwrite) {
    // 如果配置了overwrite，或者/demo_ds_ms/config/datasource节点还不存在，那么就持久化数据源相关配置；
    if (isOverwrite || !hasDataSourceConfiguration()) {
        regCenter.persist(configNode.getFullPath(ConfigurationNode.DATA_SOURCE_NODE_PATH), DataSourceJsonConverter.toJson(dataSourceMap));
    }
}
```

根据上面的分析得出数据源配置路径为：`/orchestration-yaml-test/demo_ds_ms/config/datasource`。即完整路径表达式为：`/${orchestration.zookeeper.namespace}/${orchestration.name}/config/datasource`；其他几个配置信息持久化的源码分析类似；

#### 2.2节点配置信息与源码对应关系

```
config
    ├──datasource                                persistDataSourceConfiguration()
    ├──sharding                                  
    ├      ├──rule                               persistShardingRuleConfiguration()
    ├      ├──configmap                          persistShardingConfigMap()
    ├      ├──props                              persistShardingProperties()
    ├──masterslave                               
    ├      ├──rule                               
    ├      ├──configmap  
state
    ├──instances                                persistShardingInstanceOnline()
    ├      ├──${instance1-ip}@${pid}@${uuid}                              
    ├      ├──${instance2-ip}@${pid}@${uuid}  
    ├──datasources                              persistDataSourcesNode()
```

> 说明：节点信息省略了路径前缀`/${orchestration.zookeeper.namespace}/${orchestration.name}`；例如，某instance节点的完整路径：`/${orchestration.zookeeper.namespace}/${orchestration.name}/state/instances/${ip}@${pid}@${uuid}`（/demo_ds_ms/state/instances/10.0.0.189@10072@6f8f1b1e-90a4-4edd-baf9-aeb906a664bd）；

#### 3.创建监听器

**OrchestrationFacade.init()**中调用persist***()方法持久化各配置信息到注册中心后，再调用**listenerManager.initShardingListeners(shardingDataSource)**创建监听器，核心源码如下：

```
public void initShardingListeners(final ShardingDataSource shardingDataSource) {
    // 监听三个节点(/config/datasource, /config/sharding/rule, /config/sharding/props)
    configurationListenerManager.start(shardingDataSource);
    // 监听节点/state/instances/${instance-ip}@${pid}@${uuid}，即监听表示当前实例的节点
    instanceListenerManager.start(shardingDataSource);
    // 监听节点/state/datasources
    dataSourceListenerManager.start(shardingDataSource);
    // 监听节点/config/sharding/cofigmap
    configMapListenerManager.start(shardingDataSource);
}
```

#### 3.1 rule节点监听分析

核心源码如下：

```
@Override
public void start(final ShardingDataSource shardingDataSource) {
// 节点路径增加监听器
    regCenter.watch(stateNode.getInstancesNodeFullPath(OrchestrationInstance.getInstance().getInstanceId()), new EventListener() {

        @Override
        public void onChange(final DataChangedEvent event) {
            // 当收到UPDATED类型事件
            if (DataChangedEvent.Type.UPDATED == event.getEventType()) {
                // 首先拿到所有数据源
                Map<String, DataSource> dataSourceMap = configService.loadDataSourceMap();
                // 如果具体实例的节点的value被置为disabled（大小写不敏感），那么将该实例下所有数据源置为CircuitBreakerDataSource（这是sharding-jdbc自定义的一个特殊数据源，如果SQL路由到该数据源上，那么执行时不返回任何数据，也不实际执行该SQL，相当于一个mock的数据源）
                if (StateNodeStatus.DISABLED.toString().equalsIgnoreCase(regCenter.get(event.getKey()))) {
                    for (String each : dataSourceMap.keySet()) {
                        dataSourceMap.put(each, new CircuitBreakerDataSource());
                    }
                }
                try {
                    shardingDataSource.renew(configService.loadShardingRuleConfiguration().build(dataSourceMap), configService.loadShardingProperties());
                } catch (final SQLException ex) {
                    throw new ShardingJdbcException(ex);
                }
            }
        }
    });
}
```

> 说明：将某个具体实例的节点的value置为**disabled**的命令(基于zookeeper)： **set /orchestration-spring-namespace-test/shardingDataSource/state/instances/10.52.16.134@13272@42533e85-9bb1-4484-baa1-2a2f9b2480a6 disabled**，instances后面的**10.52.16.134@13272@42533e85-9bb1-4484-baa1-2a2f9b2480a6**视具体情况而定。

#### 3.4 其他节点监听分析

其他节点监听处理和上面两个的处理逻辑几乎大同小异，监听UPDATED事件，然后从注册中心加载最新的配置后刷新数据；

### 总结

通过对源码的分析可知，sharding-jdbc的服务编排原理很简单：

1. 构造数据源；
2. 持久化sharding规则，数据源等信息到zk（或者etcd）中；
3. 对zk上的节点增加watch即监听；
4. 如果zk节点有任何变化，会刷新相关数据；