package com.leelovejava.jvm;

import com.leelovejava.essearch.EssearchApplication;
import com.leelovejava.essearch.document.ProductDocument;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EssearchApplication.class)
public class ElasticsearchTemplateTests {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    // https://blog.csdn.net/tianyaleixiaowu/article/details/77965257

    @Test
    public void testWithQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryStringQuery("无印良品 MUJI 基础润肤化妆水")).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * 单字符串模糊查询，默认排序。将从所有字段中查找包含传来的word分词后字符串的数据集
     * Sort 排序 Direction(排序规则) properties(排序字段)
     */
    @Test
    public void testQueryStringQuery() {
        //使用queryStringQuery完成单字符串查询
        String word = "无印良品 MUJI 基础润肤化妆水";
        // page从0开始
        //Pageable pageable = PageRequest.of(0,1);

        // 排序org.springframework.data.domain.Sort
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(0, 5, sort);

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryStringQuery(word)).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * 某字段按字符串模糊查询
     * 查询某个字段中模糊包含目标字符串，使用matchQuery
     */
    @Test
    public void testSearchQuery() {
        String productName = "无印良品 MUJI 基础润肤化妆水";
        Pageable pageable = PageRequest.of(0, 1);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("productName", productName)).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * PhraseMatch查询，短语匹配
     * 单字段对某短语进行匹配查询，短语分词的顺序会影响结果
     * <p>
     * 解析查询字符串来产生一个词条列表
     * 会搜索所有的词条,但只保留包含了所有搜索词条的文档,并且词条的位置要邻接
     * 一个针对短语“中华共和国”的查询不会匹配“中华人民共和国”，因为没有含有邻接在一起的“中华”和“共和国”词条
     * 这种完全匹配比较严格，类似于数据库里的“%无印良品%”这种，使用场景比较狭窄
     * matchPhraseQuery().slop(n) 分词后，中间能间隔几个位置的也能查出来
     * <p>
     * slop : http://blog.csdn.net/xifeijian/article/details/51090707
     */
    @Test
    public void testMatchPhraseQuery() {
        String productName = "无印良品 MUJI 基础润肤化妆水";
        Pageable pageable = PageRequest.of(0, 5);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("productName", productName)).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * Term查询 ==
     * http://www.cnblogs.com/muniaofeiyu/p/5616316.html
     * term匹配，即不分词匹配，你传来什么值就会拿你传的值去做完全匹配
     */
    @Test
    public void testTermQuery() {
        // 不对传来的值分词，去找完全匹配的
        String productName = "无印良品 MUJI 基础润肤化妆水";
        Pageable pageable = PageRequest.of(0, 5);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("productName", productName)).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * multi_match 多个字段匹配某字符串
     */
    @Test
    public void testMultiMatch() {
        String productName = "无印良品 MUJI 基础润肤化妆水";
        Pageable pageable = PageRequest.of(0, 5);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(multiMatchQuery(productName, "productName", "productDesc")).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * Operator 完全包含查询
     */
    @Test
    public void testOperator() {
        String productName = "无印良品 MUJI 基础润肤化妆水";
        Pageable pageable = PageRequest.of(0, 5);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("productName", productName).operator(MatchQueryBuilder.DEFAULT_OPERATOR.AND)).withPageable(pageable).build();
        List<ProductDocument> list = elasticsearchTemplate.queryForList(searchQuery, ProductDocument.class);
        print(list);
    }

    /**
     * 打印
     *
     * @param list
     */
    private void print(List<ProductDocument> list) {
        for (ProductDocument product : list) {
            System.out.println(product.getId() + ":" + product.getProductName() + ":" + product.getProductDesc());
        }
    }
}
