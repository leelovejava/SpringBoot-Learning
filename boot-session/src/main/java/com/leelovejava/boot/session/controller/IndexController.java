package com.leelovejava.boot.session.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * SpringSession测试
 *
 * @author leelovejava
 * @date 2020/04/15 15:09
 */
@RestController
public class IndexController {

    @Resource
    private HttpSession session;

    private final String key = "a:1";

    /**
     * 新增
     *
     * @return
     * @see `http://localhost:8080/put`
     */
    @RequestMapping("put")
    public String put() {
        session.setAttribute(key, String.valueOf(System.currentTimeMillis()));
        session.setMaxInactiveInterval(10);
        return key;
    }

    /**
     * 获取
     *
     * @return
     * @see `http://localhost:8080/get`
     */
    @RequestMapping("get")
    public String get() {
        return (String) session.getAttribute(key);
    }
}
