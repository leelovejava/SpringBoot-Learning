package com.leelovejava.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * hdfs测试
 * @author tianhao
 * @date 2018-09-12 09:44
 */
public class HDFSApp {
    private static final String HDFS_PATH = "hdfs://192.168.9.161:8020";
    private FileSystem fileSystem;
    private Configuration configuration;

    @Before
    public void  setup() {
        configuration = new Configuration();
        try {
            //configuration.set("HADOOP_HOME","/usr/local/hadoop");
            //configuration.set("fs.defaultFS","hdfs://192.168.109.131:9000");
            //configuration.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            //configuration.set("hadoop.home.dir", "D:/setup/hadoop");
            //configuration.set("HADOOP_HOME","D:/setup/hadoop");
            fileSystem = FileSystem.get(new URI(HDFS_PATH),configuration,"hadoop");
            //fileSystem = FileSystem.get(new URI(HDFS_PATH),configuration);
            //fileSystem = FileSystem.get(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        configuration = null;
        fileSystem = null;
        System.out.println("HDFSApp.tearDown");
    }

    /**
     * 创建hdfs目录
     * @throws IOException
     */
    @Test
    public void mkdir() throws IOException {
        fileSystem.mkdirs(new Path("hdfsApi/test"));
    }

    @Test
    public void readFileTest() {
        try {
            readFile("/hadoop000/1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取hdfs文件内容，并在控制台打印出来
     *
     * @param filePath
     * @throws IOException
     */
    public void readFile(String filePath) throws IOException {
        Configuration conf = new Configuration();
        Path srcPath = new Path(filePath);
        FileSystem fs = null;
        URI uri;
        try {
            uri = new URI(filePath);
            fs = FileSystem.get(uri, conf);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        try {
            in = fs.open(srcPath);
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }
    }



    /**
     * No FileSystem for scheme "hdfs"
     */

    /**
     * 报错“HADOOP_HOME and hadoop.home.dir are unset”
     * 下载hadoop 和 hadoop.dll 和 winutils.exe
     * conf.set("hadoop.home.dir", "D:/Developer/hadoop-2.8.4");
     */

    /**
     * Caused by: org.apache.hadoop.ipc.RpcException: RPC response exceeds maximum data length
     * hdfs 9000 拒绝连接
     * @see https://blog.csdn.net/yjc_1111/article/details/53817750
     *
     */
}
