package com.didispace;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.function.Consumer;

/**
 * 原生方式操作mongodb
 * @author leelovejava
 * @date 2020/3/27 9:43
 **/
public class NativeMethodTest {
    public static void main(String[] args) {
        // 建立连接
        MongoClient mongoClient =
                MongoClients.create("mongodb://localhost:27017");
        // 选择数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("testdb");
        // 选择表
        MongoCollection<Document> userCollection =
                mongoDatabase.getCollection("user");
        // 查询数据
        userCollection.find().limit(10).forEach((Consumer<? super Document>)
                document -> {
                    System.out.println(document.toJson());
                });

        // 查询数据
        /*userCollection.find().limit(10).forEach(new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson());
            }
        });*/
        // 关闭连接
        mongoClient.close();

    }
}
