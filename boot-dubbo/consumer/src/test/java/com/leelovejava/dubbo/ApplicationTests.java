package com.leelovejava.dubbo;

import com.leelovejava.dubbo.service.ComputeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    @Resource
    private ComputeService computeService;

    @Test
    public void testAdd() throws Exception {
        Assert.assertEquals("compute-service:add", new Integer(3), computeService.add(1, 2));
    }

}
