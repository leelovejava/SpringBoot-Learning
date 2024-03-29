package com.leelovejava.essearch.service;

import com.leelovejava.essearch.document.ProductDocument;

import java.util.List;

/**
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:32
 */
public interface EsSearchService extends BaseSearchService<ProductDocument> {
    /**
     * 保存
     *
     * @param productDocuments
     * @auther: zhoudong
     * @date: 2018/12/13 16:02
     */
    void save(ProductDocument... productDocuments);

    /**
     * 批量保存
     *
     * @param productDocuments
     */
    void bulkIndex(ProductDocument... productDocuments);

    /**
     * 删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 清空索引
     */
    void deleteAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    ProductDocument getById(String id);

    /**
     * 查询全部
     *
     * @return
     */
    List<ProductDocument> getAll();
}
