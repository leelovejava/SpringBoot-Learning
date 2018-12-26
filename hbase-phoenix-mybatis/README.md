## 技术
springBoot+mybatis+hbase+phoenix+HikariCP

## 踩坑

由于使用hbase最新包2.0,导致maven依赖的问题,解决

1) phoenix-server的版本5.0.0-HBase-2.0在中央仓库不存在,引入本地文件,从phoenix的安装包中获取
```xml
<dependency>
    <groupId>org.apache.phoenix</groupId>
    <artifactId>phoenix-server</artifactId>
    <version>5.0.0-HBase-2.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/phoenix-5.0.0-HBase-2.0-server.jar</systemPath>
</dependency>
```

2) 由于phoenix-core的版本5.0.0-HBase-2.0使用的是快照版本,下载失败,手动排除,引入其他版本
```xml
<dependencies>
    <dependency>
            <groupId>org.apache.phoenix</groupId>
            <artifactId>phoenix-core</artifactId>
            <version>5.0.0-HBase-2.0</version>
            <exclusions>
                <exclusion>
                    <groupId>org.glassfish</groupId>
                    <artifactId>javax.el</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>net.minidev</groupId>
                    <artifactId>json-smart</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.apache.hadoop</groupId>
                    <artifactId>hadoop-common</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>1.8</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-common</artifactId>
        <version>2.8.4</version>
    </dependency>
    <dependency>
        <groupId>net.minidev</groupId>
        <artifactId>json-smart</artifactId>
        <version>2.3</version>
    </dependency>
</dependencies>
```

