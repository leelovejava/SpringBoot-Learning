package com.leelovejava.es;

import com.leelovejava.essearch.ElasticSearchApplication;
import com.leelovejava.essearch.document.ProductDocument;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试ElasticsearchTemplate
 *
 * @author leelovejava
 * @date 2020/3/9 23:21
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticSearchApplication.class)
public class ElasticTemplateTest {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 测试Resource过滤器
     */
    @Test
    public void testResourceFilter() {
        String keyword = "无印良品";
        SourceFilter sourceFilter = new FetchSourceFilter(null, new String[]{"createTime"});
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new QueryStringQueryBuilder(keyword))
                .withSourceFilter(sourceFilter)

                .build();

        List<ProductDocument> productDocuments = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        productDocuments.forEach(item ->
                System.out.println(item.getId())
        );
    }
}
