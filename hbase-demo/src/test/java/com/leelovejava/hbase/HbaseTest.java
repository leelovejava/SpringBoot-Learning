package com.leelovejava.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HbaseTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Before
    public void  setup() {
        HBaseConn.getHBaseConn();
    }
    @Test
    public void getTable(){
        try {
            Table table =HBaseConn.getTable("test");
            System.out.println("tableName:"+table.getName());
            System.out.println("nameAsString:"+table.getName().getNameAsString());
            //System.out.println("columnFamilyCount:"+table.getDescriptor().getColumnFamilyCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Configuration config = null;
    static {
        config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "192.168.109.131:2181");
    }

    /**
     * 创建表
     */
    @Test
    public void creatTable(){
        TableName tableName = TableName.valueOf("test");
        /*try(HBaseAdmin admin =new HBaseAdmin(config)) {

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            /*if (admin.tableExists(tableName)) {
                System.out.println("table exists");
            }*/
            System.out.println("admin start");
            //System.out.println(admin.balance());
            System.out.println("tableExists:"+admin.tableExists(tableName));
            // 表描述
            HTableDescriptor desc=new HTableDescriptor(tableName);
            // 列族
            HColumnDescriptor cf = new HColumnDescriptor("cf".getBytes());
            desc.addFamily(cf);
            admin.createTable(desc);
            System.out.println("admin end");
        } catch (Exception e) {
            e.printStackTrace();
         }
    }

    @After
    public void tearDown() {
        HBaseConn.closeConn();
    }
}
