package com.leelovejava.hadoop;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @program: hadoop-train
 * @description: 使用Spring Hadoop来访问HDFS文件系统
 * @author: tianhao
 * @create: 2018-04-04 17:39
 **/
public class SpringHadoopApp {

    private ApplicationContext ctx;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        ctx = new ClassPathXmlApplicationContext("beans.xml");
        fileSystem = (FileSystem) ctx.getBean("fileSystem");
    }

    @After
    public void tearDown() throws IOException {
        ctx = null;
        fileSystem.close();
    }

    /**
     * 在HDFS上创建一个目录
     * @throws Exception
     */
    @Test
    public void testMkdirs()throws Exception{
        fileSystem.mkdirs(new Path("/SpringHDFS/"));
    }

    /**
     * 读取HDFS上的文件内容
     * @throws Exception
     */
    @Test
    public void testText()throws Exception{
        FSDataInputStream in = fileSystem.open(new Path("/SpringHDFS/1.txt"));
        IOUtils.copyBytes(in, System.out, 1024);
        in.close();
    }

    /**
     * 异常
     * 1.连接拒绝:
     *   1).修改/etc/hosts 0.0.0.0 hadoop001
     *   2).修改hadoop/etc/hadoop/core-site.xml
     * 2.org.apache.hadoop.hdfs.server.namenode.SafeModeException: Cannot create directory /SpringHDFS. Name node is in safe mode
     * 关闭安全模式
     *   bin/hadoop dfsadmin -safemode leave
     */
}