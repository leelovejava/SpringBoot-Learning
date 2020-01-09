package com.didispace;

import com.alibaba.fastjson.JSON;
import com.didispace.domain.User;
import com.didispace.domain.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    @Resource
    private UserRepository userRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void test() throws Exception {

        // 创建三个User，并验证User总数
        userRepository.save(new User(1L, "didi", 30));
        userRepository.save(new User(2L, "mama", 40));
        userRepository.save(new User(3L, "kaka", 50));
        Assert.assertEquals(3, userRepository.findAll().size());

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

}
