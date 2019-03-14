package com.leelovejava.hbase;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;

/**
 * hbase优化
 */
public class HbaseOptimiza {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Connection connection;

    @Before
    public void setup() {
        connection = HBaseConn.getHBaseConn();
    }

    /**
     * 1).创建预分区
     * 默认情况下，在创建HBase表的时候会自动创建一个region分区，
     * 当导入数据的时候，所有的HBase客户端都向这一个region写数据，
     * 直到这个region足够大了才进行切分。
     * <p>
     * 一种可以加快批量写入速度的方法是通过预先创建一些空的regions，
     * 这样当数据写入HBase时，会按照region分区情况，在集群内做数据的负载均衡。
     *
     * @throws IOException
     */
    @Test
    public void creatingPreRegions() throws IOException {
        Admin admin = connection.getAdmin();
        byte[][] splits = getHexSplits("100", "200", 2);
        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf("test2"));
        admin.createTable(descriptor, splits);
    }

    public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
        //start:001,endkey:100,10region [001,010] [011, 020]
        byte[][] splits = new byte[numRegions - 1][];
        BigInteger lowestKey = new BigInteger(startKey, 16);
        BigInteger highestKey = new BigInteger(endKey, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
        lowestKey = lowestKey.add(regionIncrement);
        for (int i = 0; i < numRegions - 1; i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }

    @After
    public void tearDown() {
        HBaseConn.closeConn();
    }
}
