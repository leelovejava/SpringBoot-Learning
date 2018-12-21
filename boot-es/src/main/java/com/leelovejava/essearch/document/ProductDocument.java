package com.leelovejava.essearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品实体
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:22
 */
@Document(indexName = "orders", type = "product")
// 解决IK分词不能使用问题
@Mapping(mappingPath = "productIndex.json")
public class ProductDocument implements Serializable {

    /**
     * Document
     *  indexName        索引库的名称,个人建议以项目的名称命名
     *  type             类型，个人建议以实体的名称命名
     *  shards           默认分区数 5
     *  replicas         每个分区默认的备份数 1
     *  refreshInterval  刷新间隔 1s
     *  indexStoreType   索引文件存储类型 fs
     *
     * Field
     *  type             自动检测属性的类型  FieldType.Auto
     *  index            默认情况下分词      FieldIndex.analyzed
     *  format           DateFormat.none
     *  pattern
     *  store            默认情况下不存储原文 false
     *  searchAnalyzer   指定字段搜索时使用的分词器
     *  indexAnalyzer    指定字段建立索引时指定的分词器
     *  ignoreFields     如果某个字段需要被忽略
     *  includeInParent  false
     */

    @Id
    private String id;
    //@Field(analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String productName;
    //@Field(analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String productDesc;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
