package com.leelovejava.hbase;


import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by jixin on 18-2-25.
 * 过滤器
 */
public class HBaseFilterTest {

    @Test
    public void createTable() {
        HBaseUtil.createTable("FileTable", new String[]{"fileInfo", "saveInfo"});
    }

    @Test
    public void addFileDetails() {
        HBaseUtil.putRow("FileTable", "rowkey1", "fileInfo", "name", "file1.txt");
        HBaseUtil.putRow("FileTable", "rowkey1", "fileInfo", "type", "txt");
        HBaseUtil.putRow("FileTable", "rowkey1", "fileInfo", "size", "1024");
        HBaseUtil.putRow("FileTable", "rowkey1", "saveInfo", "creator", "jixin");
        HBaseUtil.putRow("FileTable", "rowkey2", "fileInfo", "name", "file2.jpg");
        HBaseUtil.putRow("FileTable", "rowkey2", "fileInfo", "type", "jpg");
        HBaseUtil.putRow("FileTable", "rowkey2", "fileInfo", "size", "1024");
        HBaseUtil.putRow("FileTable", "rowkey2", "saveInfo", "creator", "jixin");
        HBaseUtil.putRow("FileTable", "rowkey3", "fileInfo", "name", "file3.jpg");
        HBaseUtil.putRow("FileTable", "rowkey3", "fileInfo", "type", "jpg");
        HBaseUtil.putRow("FileTable", "rowkey3", "fileInfo", "size", "1024");
        HBaseUtil.putRow("FileTable", "rowkey3", "saveInfo", "creator", "jixin");

    }

    /**
     * rowKey过滤器—RowFilter
     * 通过正则，过滤rowKey值
     *
     * 　SingleColumnValueFilter
     *
     * 　　参数：列族、列名、操作符、列值
     *
     * 　　操作符可以为：
     *
     * 　　CompareOp.LESS：小于
     *
     * 　　CompareOp.LESS_OR_EQUAL：小于或者等于
     *
     * 　　CompareOp.EQUAL：等于
     *
     * 　　CompareOp.NOT_EQUAL：不等于
     *
     * 　　CompareOp.GREATER_OR_EQUAL：大于或者等于
     *
     * 　　CompareOp.GREATER：大于
     *
     * 　　CompareOp.NO_OP：不比较
     */
    @Test
    public void rowFilterTest() {
        // 列值过滤器 对列值进行过滤
        // CompareOperator op, ByteArrayComparable rowComparator
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("rowkey1")));

        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(filter));

        ResultScanner scanner = HBaseUtil
                .getScanner("FileTable", "rowkey1", "rowkey3", filterList);

        print(scanner);
    }

    /**
     * 前缀过滤器:过滤指定列名
     */
    @Test
    public void prefixFilterTest() {
        Filter filter = new PrefixFilter(Bytes.toBytes("rowkey2"));
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(filter));
        ResultScanner scanner = HBaseUtil
                .getScanner("FileTable", "rowkey1", "rowkey3", filterList);

        print(scanner);

    }

    /**
     * 返回每行的行键,值全部为空
     * 忽略掉其值就可以减少传递到客户端的数据量
     */
    @Test
    public void keyOnlyFilterTest() {
        Filter filter = new KeyOnlyFilter(true);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(filter));
        ResultScanner scanner = HBaseUtil
                .getScanner("FileTable", "rowkey1", "rowkey3", filterList);

        print(scanner);
    }

    /**
     * 列名前缀过滤器
     * 过滤指定前缀的列名
     */
    @Test
    public void columnPrefixFilterTest() {
        Filter filter = new ColumnPrefixFilter(Bytes.toBytes("nam"));
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(filter));
        ResultScanner scanner = HBaseUtil
                .getScanner("FileTable", "rowkey1", "rowkey3", filterList);

        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey=" + Bytes.toString(result.getRow()));
                System.out.println("fileName=" + Bytes
                        .toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
                System.out.println("fileType=" + Bytes
                        .toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("type"))));
            });
            scanner.close();
        }
    }

    /**
     * 输出
     * @param scanner
     */
    private void print(ResultScanner scanner) {
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey=" + Bytes.toString(result.getRow()));
                System.out.println("fileName=" + Bytes
                        .toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
            scanner.close();
        }
    }
}
