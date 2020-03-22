package com.leelovejava.essearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.leelovejava.essearch.document.ProductDocument;
import com.leelovejava.essearch.repository.ProductDocumentRepository;
import com.leelovejava.essearch.service.EsSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * elasticsearch 搜索引擎 service实现
 *
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:33
 */
@Service
public class EsSearchServiceImpl extends BaseSearchServiceImpl<ProductDocument> implements EsSearchService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private ProductDocumentRepository productDocumentRepository;

    @Override
    public void save(ProductDocument... productDocuments) {
        elasticsearchTemplate.putMapping(ProductDocument.class);
        if (productDocuments.length > 0) {
            /*Arrays.asList(productDocuments).parallelStream()
                    .map(productDocumentRepository::save)
                    .forEach(productDocument -> log.info("【保存数据】：{}", JSON.toJSONString(productDocument)));*/
            log.info("【保存索引】：{}", JSON.toJSONString(productDocumentRepository.saveAll(Arrays.asList(productDocuments))));
        }
    }

    /**
     * 批量保存
     *
     * @param productDocuments
     */
    @Override
    public void bulkIndex(ProductDocument... productDocuments) {
        List<IndexQuery> queries = new ArrayList<>();
        IndexQuery indexQuery;

        for (ProductDocument document : productDocuments) {
            indexQuery = new IndexQuery();
            indexQuery.setObject(document);
            queries.add(indexQuery);
        }
        elasticsearchTemplate.bulkIndex(queries);
        queries.clear();
    }

    @Override
    public void delete(String id) {
        productDocumentRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        productDocumentRepository.deleteAll();
    }

    @Override
    public ProductDocument getById(String id) {
        return productDocumentRepository.findById(id).get();
    }

    @Override
    public List<ProductDocument> getAll() {
        List<ProductDocument> list = new ArrayList<>();
        productDocumentRepository.findAll().forEach(list::add);
        return list;
    }
    /**
     * matchAllQuery    查询所有
     * queryStringQuery 对所有字段分词查询
     * wildcardQuery    通配符查询
     * TermQuery        词条查询
     * fuzzy            模糊查询
     */

}
