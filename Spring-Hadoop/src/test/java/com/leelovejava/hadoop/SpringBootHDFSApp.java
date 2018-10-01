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
        //System.setProperty("hadoop.home.dir","D:/setup/hadoop");
        //System.setProperty("HADOOP_USER_NAME","hadoop");
        // 查看根目录下的所有文件
        for (FileStatus fileStatus : fsShell.ls("/")) {
            System.out.println("> " + fileStatus.getPath());
        }
        // 创建文件夹
        //fsShell.mkdir("/hadoop001/");

        // 删除文件
        fsShell.rm("/jsonp.txt");

        // 上传文件
        fsShell.put("D:/jsonp.txt","/");



        // 下载文件
        fsShell.get("/jsonp.txt","C://");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHDFSApp.class, args);
    }

    /**
     * Caused by: org.apache.hadoop.ipc.RemoteException(java.io.IOException): File /hadoop001/3.zip could only be written to 0 of the 1 minReplication nodes. There are 1 datanode(s) running and 1 node(s) are excluded in this operation.
     *
     * Failed to connect to /127.0.0.1:9866 for block BP-806866622-0.0.0.0-1537509597269:blk_1073741841_1018, add to deadNodes and continue.
     *
     * 3.上传文件异常:
     * HDFS客户端的权限错误
     *  Permission denied: user=Administrator, access=WRITE, inode="/":hadoop:supergroup:drwxr-xr-x
     *  1) 修改hadoop目录的权限
     *  sudo chmod -R 755 /home/hadoop/
     *  2) 修改hdfs的权限:sudo bin/hadoop dfs -chmod -R 755 /
     *  https://blog.csdn.net/xianjie0318/article/details/75453758/
     *  3) hadoop/etc/core-sit.xml
     *  <property>
             <name>dfs.permissions</name>
             <value>false</value>
         </property>
     */
}