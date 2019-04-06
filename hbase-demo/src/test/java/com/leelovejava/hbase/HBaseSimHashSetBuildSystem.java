package com.leelovejava.hbase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


/**
 * 基于协处理器构建百亿级文本去重系统
 *
 * @see 'https://mp.weixin.qq.com/s/UZmksFBTKFzF4jrXGnw2fg'
 * <p>
 * 官网 https://blogs.apache.org/hbase/entry/coprocessor_introduction
 * 使用HBase协处理器---基本概念和regionObserver的简单实现 https://blog.csdn.net/m0_37739193/article/details/76917881
 * 分类：  Observers和Endpoint
 * Observers: 问传统数据库的触发器，当发生某一个特定操作的时候出发Observer
 */
public class HBaseSimHashSetBuildSystem extends BaseRegionObserver {

    private Logger logger = LoggerFactory.getLogger(HBaseSimHashSetBuildSystem.class);

    /**
     * 协处理器是运行于region中的，每一个region都会加载协处理器
     * 这个方法会在regionserver打开region时候执行（还没有真正打开）
     *
     * @param e
     * @throws IOException
     */
    @Override
    public void start(CoprocessorEnvironment e) {
        logger.info("Coprocessor opration start...");
    }

    /**
     * @param e
     * @param put
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        // test flag
        logger.info("do something before Put Opration...");

        List<Cell> cells = put.get(Bytes.toBytes("f"), Bytes.toBytes("si"));
        if (cells == null || cells.size() == 0) {
            return;
        }
        String simhash__itemid = Bytes.toString(CellUtil.cloneValue(cells.get(0)));
        if (StringUtils.isEmpty(simhash__itemid) || simhash__itemid.split("__").length != 2) {
            return;
        }
        String simhash = simhash__itemid.trim().split("__")[0];
        String itemid = simhash__itemid.trim().split("__")[1];

        // 获取Put Rowkey
        byte[] row = put.getRow();
        // 通过Rowkey构造Get对象
        Get get = new Get(row);
        get.setMaxVersions(1);
        get.addFamily(Bytes.toBytes("f"));
        Result result = e.getEnvironment().getRegion().get(get);
        Cell columnCell = result.getColumnLatestCell(Bytes.toBytes("f"), Bytes.toBytes("s0")); // set size
        if (columnCell == null) {
            // 第一次存储数据，将size初始化为1
            logger.info("第一次存储数据，将size初始化为1");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(simhash, itemid);
            Gson gson = new Gson();
            String json = gson.toJson(jsonObject);

            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("s1"), Bytes.toBytes(json)); // json 数组
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("s0"), Bytes.toBytes("1"));  // 初始化
        } else {
            byte[] sizebyte = CellUtil.cloneValue(columnCell);
            int size = Integer.parseInt(Bytes.toString(sizebyte));
            logger.info("非第一次存储数据 ----> Rowkey `" + Bytes.toString(row) + "` simhash set size is : " + size + ", the current value is : " + simhash__itemid);
            for (int i = 1; i <= size; i++) {
                Cell cell1 = result.getColumnLatestCell(Bytes.toBytes("f"), Bytes.toBytes("s" + i));
                String jsonBefore = Bytes.toString(CellUtil.cloneValue(cell1));
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonBefore, JsonObject.class);
                int sizeBefore = jsonObject.entrySet().size();
                if (i == size) {
                    if (!jsonObject.has(simhash)) {
                        if (sizeBefore == 10000) {
                            JsonObject jsonone = new JsonObject();
                            jsonone.addProperty(simhash, itemid);
                            String jsonstrone = gson.toJson(jsonone);
                            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("s" + (size + 1)), Bytes.toBytes(jsonstrone)); // json 数组
                            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("s0"), Bytes.toBytes((size + 1) + ""));  // 初始化
                        } else {
                            jsonObject.addProperty(simhash, itemid);
                            String jsonAfter = gson.toJson(jsonObject);
                            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("s" + size), Bytes.toBytes(jsonAfter)); // json 数组
                        }
                    } else {
                        return;
                    }
                } else {
                    if (!jsonObject.has(simhash)) {
                        continue;
                    } else {
                        return;
                    }
                }
            }
        }
    }
}