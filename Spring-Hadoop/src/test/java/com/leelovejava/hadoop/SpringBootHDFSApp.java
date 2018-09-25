package com.leelovejava.hadoop;

import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.hadoop.fs.FsShell;

/**
 * @program: hadoop-train
 * @description: 使用spring boot来访问HDFS
 * @author: 01
 * @create: 2018-04-04 18:45
 **/
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SpringBootHDFSApp implements CommandLineRunner {

    @Autowired
    private FsShell fsShell;  // 用于执行hdfs shell命令的对象

    public void run(String... strings) throws Exception {
        // 查看根目录下的所有文件
        for (FileStatus fileStatus : fsShell.ls("/")) {
            System.out.println("> " + fileStatus.getPath());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHDFSApp.class, args);
    }
}