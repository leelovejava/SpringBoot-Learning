package com.atguigu.java;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

/**
 * 标准Java异步HTTP客户端。
 *
 * @author leelovejava
 */
public class HttpClientTest {

    /**
     * 异步
     *
     * @throws Exception
     */
    @Test
    public void testName2() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080/test/")).build();
        BodyHandler<String> responseBodyHandler = BodyHandlers.ofString();
        CompletableFuture<HttpResponse<String>> sendAsync = client.sendAsync(request, responseBodyHandler);
        sendAsync.thenApply(t -> t.body()).thenAccept(System.out::println);
        //HttpResponse<String> response = sendAsync.get();
        //String body = response.body();
        //System.out.println(body);


        /// 返回的是 future，然后通过 future 来获取结果
        CompletableFuture<String> future =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body);
        /// 阻塞线程，从 future 中获取结果
        String body = future.get();

    }

    /**
     * 同步
     *
     * @throws Exception
     */
    @Test
    public void testName() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://openjdk.java.net/"))
                // 存入消息头
                // 消息头是保存在一张 TreeMap 里的
                .header("Content-Type", "application/json")
                //http 协议版本
                .version(HttpClient.Version.HTTP_2)
                // 发起一个 post 消息，需要存入一个消息体
                .POST(HttpRequest.BodyPublishers.ofString("hello"))
                // 发起一个 get 消息，get 不需要消息体
                //.GET()
                //method(...) 方法是 POST(...) 和 GET(...) 方法的底层，效果一样
                //.method("POST",HttpRequest.BodyPublishers.ofString("hello"))
                .build();
        BodyHandler<String> responseBodyHandler = BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, responseBodyHandler);
        String body = response.body();
        System.out.println(body);
    }
}
