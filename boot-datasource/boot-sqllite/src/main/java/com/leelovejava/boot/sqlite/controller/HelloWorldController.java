package com.leelovejava.boot.sqlite.controller;

import com.leelovejava.boot.sqlite.model.HelloModel;
import com.leelovejava.boot.sqlite.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author leelovejava
 * @see 'https://www.cnblogs.com/oukele/p/9590544.html' 一分钟学会在IDEA中使用sqlite数据库
 * @see 'https://segmentfault.com/a/1190000016176354' Spring Boot操作Sqlite数据库 从入门到跑路
 */
@RestController
public class HelloWorldController {
    private final HelloService helloService;

    @Autowired
    public HelloWorldController(HelloService HelloService) {
        this.helloService = HelloService;

    }

    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }

    /**
     * http://localhost:8080/list
     *
     * @return
     */
    @RequestMapping("/list")
    public List<HelloModel> list() {
        return helloService.selectAll();
    }

    /**
     * http://localhost:8080/insert
     * {
     *     "title": "张三",
     *     "text": "cccccccc"
     * }
     * @return
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public String insert(@RequestBody HelloModel model) {
        helloService.insert(new HelloModel(System.currentTimeMillis(),
                model.getTitle(), model.getText()));
        return "success";
    }
}
