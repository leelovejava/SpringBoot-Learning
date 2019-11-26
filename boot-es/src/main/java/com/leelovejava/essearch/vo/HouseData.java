package com.leelovejava.essearch.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author leelovejava
 * @date 2019/11/2 22:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "haoke", type = "house", createIndex = false)
public class HouseData {
    @Id
    private String id;
    private String title;
    private String rent;
    private String floor;
    private String image;
    private String orientation;
    private String houseType;
    private String rentMethod;
    private String time;
}
