package com.leelovejava.boot.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leelovejava
 * @date 2020/11/12 21:27
 **/
@RestController
@RequestMapping
public class HelloController {
    @GetMapping
    public String hello() {
        return "Hello,From Docker!";
    }
}
