package com.leelovejava.boot.mysql.sharding.sphere;

import com.leelovejava.boot.mysql.model.OrderModel;
import com.leelovejava.boot.mysql.service.OrderService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderSpringBootTests.class)
@SpringBootApplication
@ActiveProfiles("sharding")
public class OrderSpringBootTests {

    @Resource
    private OrderService orderService;

    @Test
    public void save(){
        List<String> merchantList = Lists.newArrayList("aliyun", "taobao", "tmall");
        // 测试20笔订单，且所属商户随机产生
        for (int i = 0; i < 20; i++) {
            OrderModel order = new OrderModel();
            order.setUserId(i);
            order.setOrderNo(System.currentTimeMillis() + String.format("%06d", i));
            order.setOrderTime(new Date());
            order.setMerchant(merchantList.get(new Random().nextInt(merchantList.size())));
            orderService.save(order);

        }
    }

}