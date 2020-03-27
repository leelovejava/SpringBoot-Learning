package com.didispace;

import com.alibaba.fastjson.JSON;
import com.didispace.domain.User;
import com.didispace.domain.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MongodbApplication.class)
public class ApplicationTests {

    @Resource
    private UserRepository userRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        /// userRepository.deleteAll();
    }

    @Test
    public void test() throws Exception {

        insert();


        // 删除一个User，再验证User总数
        ///Optional one = userRepository.findOne(1L);
        User u = userRepository.findById(1L).get();
        userRepository.delete(u);
        Assert.assertEquals(2, userRepository.findAll().size());

        // 删除一个User，再验证User总数
        u = userRepository.findByUsername("mama");
        userRepository.delete(u);
        Assert.assertEquals(1, userRepository.findAll().size());

    }

    /**
     * 关联查询
     */
    @Test
    public void test02() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                // 关联从表名
                .from("grade")
                // 主表关联字段
                .localField("gradeId")
                // 从表关联的字段
                .foreignField("_id")
                //查询结果名
                .as("GradeAndStu");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
        List<Map> results = mongoTemplate.aggregate(aggregation, "student", Map.class).getMappedResults();
        // 上面的student必须是查询的主表名
        System.out.println(JSON.toJSONString(results));
    }

    @Test
    public void testInsert() {
        insert();
    }

    /**
     * 分页查询
     */
    @Test
    public void test03() {
        String userName = "zhangsan";
        Long age = 22L;
        int page = 1;
        int rows = 2;

        // where username=? or age=?
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("age").is(age),
                Criteria.where("username").is(userName)
        );

        // 分页和排序
        // page从0开始
        PageRequest pageRequest = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "id"));
        Query query = Query.query(criteria).with(pageRequest);
        mongoTemplate.find(query, User.class).forEach(item -> System.out.println(item.getAge()));
    }

    private void insert() {
        // 创建三个User，并验证User总数
        userRepository.save(new User(1L, "didi", 22));
        userRepository.save(new User(2L, "zhangsan", 40));
        userRepository.save(new User(3L, "kaka", 10));
        userRepository.save(new User(4L, "spring", 20));
        userRepository.save(new User(5L, "mama", 99));

        Assert.assertEquals(5, userRepository.findAll().size());
    }

}
