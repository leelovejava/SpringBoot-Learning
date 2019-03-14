package com.leelovejava.hbase;

import com.google.protobuf.InvalidProtocolBufferException;
import com.leelovejava.hbase.util.RandomValueUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Before
    public void setup() {
        HBaseConn.getHBaseConn();
    }

    @Test
    public void getTable() {
        try {
            Table table = HBaseConn.getTable("test");
            System.out.println("tableName:" + table.getName());
            System.out.println("nameAsString:" + table.getName().getNameAsString());
            //System.out.println("columnFamilyCount:"+table.getDescriptor().getColumnFamilyCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Configuration config;

    static {
        config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "hadoop000:2181");
    }

    /**
     * 创建表
     */
    @Test
    public void creatTable() {
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
            System.out.println("tableExists:" + admin.tableExists(tableName));
            // 表描述
            HTableDescriptor desc = new HTableDescriptor(tableName);
            // 列族
            HColumnDescriptor cf = new HColumnDescriptor("cf".getBytes());
            desc.addFamily(cf);
            admin.createTable(desc);
            System.out.println("admin end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * hbase的protobuf存储
     */
    @Test
    public void insertHbaseProtoBuf() {
        try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            // 1).创建表
            String tableName = "phone";
            // 表描述
            HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
            // 列族
            HColumnDescriptor cf = new HColumnDescriptor("cf".getBytes());
            desc.addFamily(cf);
            admin.createTable(desc);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // 2).插入数据
            List<Put> puts = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                // 手机号码
                String phoneNum = RandomValueUtil.getTel();
                for (int j = 0; j < 100; j++) {
                    String dnum = RandomValueUtil.getTel();
                    // 通话时长
                    String length = RandomValueUtil.getCallLength();
                    // 类型:主叫/被叫
                    String type = RandomValueUtil.getType();
                    String dateStr = RandomValueUtil.randomDate("2000-01-01", "2019-03-10");
                    String rowKey = phoneNum + "_" + (Long.MAX_VALUE - sdf.parse(dateStr).getTime());
                    Phone.PhoneDetail.Builder phoneDetail = Phone.PhoneDetail.newBuilder();
                    phoneDetail.setDate(dateStr);
                    phoneDetail.setDnum(dnum);
                    phoneDetail.setLength(length);
                    phoneDetail.setType(type);

                    Put put = new Put(rowKey.getBytes());
                    put.addColumn("cf".getBytes(), "phoneDetail".getBytes(), phoneDetail.build().toByteArray());
                    puts.add(put);
                }
            }
            HBaseUtil.putRows(tableName, puts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 有十个用户，每个用户每天产生100条记录，将100条记录放到一个集合进行存储
     *
     * @throws Exception
     */
    @Test
    public void insertProtobufList() throws Exception {
        List<Put> puts = new ArrayList<>();
        String tableName = "phone";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10000; i++) {
            String phoneNum = RandomValueUtil.getTel();
            String rowkey = phoneNum + "_" + (Long.MAX_VALUE - sdf.parse("2019-01-01").getTime());
            Phone2.dayPhoneDetail.Builder dayPhone = Phone2.dayPhoneDetail.newBuilder();
            for (int j = 0; j < 100; j++) {
                String dnum = RandomValueUtil.getTel();
                String length = RandomValueUtil.getCallLength();
                String type = RandomValueUtil.getType();
                String dateStr = RandomValueUtil.randomDate("2000-01-01", "2019-03-10");
                Phone2.PhoneDetail2.Builder phoneDetail = Phone2.PhoneDetail2.newBuilder();
                phoneDetail.setDate(dateStr);
                phoneDetail.setDnum(dnum);
                phoneDetail.setLength(length);
                phoneDetail.setType(type);
                dayPhone.addDayPhoneDetail(phoneDetail);
            }
            Put put = new Put(rowkey.getBytes());
            put.addColumn("cf".getBytes(), "day".getBytes(), dayPhone.build().toByteArray());
            puts.add(put);
        }
        HBaseUtil.putRows(tableName, puts);
    }

    /**
     * 获取hbase的protobuf数据
     * 存在问题:同一天的数据,rowKey相同,value不同,rowKey比value占用空间更大
     */
    @Test
    public void getHbaseProtobuf() throws InvalidProtocolBufferException {
        Result result = HBaseUtil.getRow("phone", "15500220022_9223371028768375807");
        Cell cell = result.getColumnLatestCell("cf".getBytes(), "phoneDetail".getBytes());
        Phone.PhoneDetail dayPhone = Phone.PhoneDetail.parseFrom(CellUtil.cloneValue(cell));
        System.out.println(dayPhone.getDnum());
    }

    /**
     * 获取hbase中protobuf集合的数据
     *
     * @throws Exception
     */
    @Test
    public void getProtobufList() throws Exception {
        Result result = HBaseUtil.getRow("phone", "15908852295_9223370490582775807");
        Cell cell = result.getColumnLatestCell("cf".getBytes(), "day".getBytes());
        Phone2.dayPhoneDetail dayPhone = Phone2.dayPhoneDetail.parseFrom(CellUtil.cloneValue(cell));
        for (Phone2.PhoneDetail2 pd : dayPhone.getDayPhoneDetailList()) {
            System.out.println(pd);
        }
    }

    @After
    public void tearDown() {
        HBaseConn.closeConn();
    }
}
