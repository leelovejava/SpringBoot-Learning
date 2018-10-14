package com.leelovejava.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Created by jixin on 18-2-25.
 * 基于hbase-client 2.1.0
 */
public class HBaseUtil_2 {

    /**
     * 创建HBase表.
     *
     * @param tableName 表名
     * @param cfs       列族的数组
     * @return 是否创建成功
     */
    /*public static boolean createTable(TableName tableName, String[] cfs) {
        try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            // 表描述类(类包含了表的名字以及表的列族信息)
            //HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            // As of release 2.0.0, this will be removed in HBase 3.0.0. Use TableDescriptorBuilder to build HTableDescriptor.

            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName);


        *//*Arrays.stream(cfs).forEach(cf -> {
           HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
            columnDescriptor.setMaxVersions(1);
            tableDescriptor.addFamily(columnDescriptor);
        });*//*

        *//*TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName)
                .addColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("base")).build())
                .addColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("address")).build())
                .build();*//*
            List<ColumnFamilyDescriptor> families = new ArrayList<>();
            Arrays.stream(cfs).forEach(cf -> {
                // 列族的描述类:维护列族的信息
                families.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build());
            });
            // 设置列族: setColumnFamilies(Collection<ColumnFamilyDescriptor> families)
            tableDescriptorBuilder.setColumnFamilies(families);
            TableDescriptor tableDescriptor = tableDescriptorBuilder.build();
            admin.createTable(tableDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    *//**
     * 删除hbase表.
     *
     * @param tableName 表名
     * @return 是否删除成功
     *//*
    public static boolean deleteTable(TableName tableName) {
        try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    *//**
     * hbase插入一条数据.
     *
     * @param tableName 表名
     * @param rowKey    唯一标识
     * @param cfName    列族名
     * @param qualifier 列标识
     * @param data      数据
     * @return 是否插入成功
     *//*
    public static boolean putRow(String tableName, String rowKey, String cfName, String qualifier,
                                 String data) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier), Bytes.toBytes(data));
            table.put(put);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return true;
    }

    *//**
     * 写入数据操作
     * 在写入数据时，我们需要首先获取到需要操作的Table对象。
     * 然后创建一个Put对象来执行更新操作，创建对象时需要给定一个行名。
     * 然后在Put对象中添加需要执行的操作，这里是添加数据。
     * 数据填充完后，在表上执行put操作。
     * 最后，不要忘了关闭表。
     *
     * @param tableName
     * @param puts
     * @return
     *//*
    public static boolean putRows(String tableName, List<Put> puts) {
        try (Table table = HBaseConn.getTable(tableName)) {
            table.put(puts);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return true;
    }

    *//**
     * 获取单条数据.
     *
     * @param tableName 表名
     * @param rowKey    唯一标识
     * @return 查询结果
     *//*
    public static Result getRow(String tableName, String rowKey) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.get(get);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    *//**
     * 获取数据
     * 需要获取到需要操作的Table对象。
     * 创建Get对象来执行获取操作，创建Get对象的时候需要告诉它是要获取哪一行数据。
     * 然后在表上执行get操作来获取数据。
     * 取到数据后，数据是保持在Result对象中，我们可以通过Result对象的一些方法来取得需要的值。
     * 最后，不要忘了关闭表。
     *
     * 过滤器的种类：
     *  列植过滤器—SingleColumnValueFilter
     *       过滤列植的相等、不等、范围等
     *  列名前缀过滤器—ColumnPrefixFilter
     *       过滤指定前缀的列名
     *  多个列名前缀过滤器—MultipleColumnPrefixFilter
     *        过滤多个指定前缀的列名
     *  rowKey过滤器—RowFilter
     *       通过正则，过滤rowKey值。
     *
     * @param tableName
     * @param rowKey
     * @param filterList 过滤器列表
     * @return
     *//*
    public static Result getRow(String tableName, String rowKey, FilterList filterList) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.setFilter(filterList);
            return table.get(get);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    *//**
     * 查询数据
     * hbase查询数据的两种方式:
     * 1、按指定RowKey获取唯一一条记录，get方法（org.apache.hadoop.hbase.client.Get）
     * 2、按指定的条件获取一批记录，scan方法（org.apache.Hadoop.Hbase.client.Scan）  ==>scan
     * @param tableName 表名
     * @return
     *//*
    public static ResultScanner getScanner(String tableName) {
        try (Table table = HBaseConn.getTable(tableName)) {
            // scan:通过对表的扫描来获取对用的值
            Scan scan = new Scan();
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    *//**
     * 批量检索数据.
     *
     * @param tableName   表名
     * @param startRowKey 起始RowKey
     * @param endRowKey   终止RowKey
     * @return ResultScanner实例
     *//*
    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Scan scan = new Scan();
            // 限定范围,范围越小,性能越高
            scan.withStartRow(Bytes.toBytes(startRowKey));
            scan.withStopRow(Bytes.toBytes(endRowKey));
            // 设置缓存大小,提高查询效率
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    *//**
     * 批量检索数据.
     * @param tableName     表名
     * @param startRowKey   起始RowKey
     * @param endRowKey     终止RowKey
     * @param filterList    过滤器
     * @return
     *//*
    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey,
                                           FilterList filterList) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Scan scan = new Scan();
            scan.withStartRow(Bytes.toBytes(startRowKey));
            scan.withStopRow(Bytes.toBytes(endRowKey));
            scan.setFilter(filterList);
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    *//**
     * HBase删除一行记录.
     *
     * @param tableName 表名
     * @param rowKey    唯一标识
     * @return 是否删除成功
     *//*
    public static boolean deleteRow(String tableName, String rowKey) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return true;
    }

    *//**
     * 删除列族
     *
     * @param tableName
     * @param cfName    -->byte[] columnFamily
     * @return
     *//*
    public static boolean deleteColumnFamily(TableName tableName, String cfName) {
        try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            // admin.deleteColumn(tableName, cfName);
            admin.deleteColumnFamily(tableName, cfName.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    *//**
     * @param tableName
     * @param rowKey
     * @param cfName
     * @param qualifier 列标识
     * @return
     *//*
    public static boolean deleteQualifier(String tableName, String rowKey, String cfName,
                                          String qualifier) {
        try (Table table = HBaseConn.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier));
            table.delete(delete);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return true;
    }*/
    // 检查表是否可用 admin.isTableAvailable(Bytes.toBytes(tableName));
}
