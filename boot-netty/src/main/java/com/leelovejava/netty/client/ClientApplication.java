package com.leelovejava.netty.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端 启动类
 *
 * @author leelovejava
 */
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        // 启动netty客户端
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
    }
}
