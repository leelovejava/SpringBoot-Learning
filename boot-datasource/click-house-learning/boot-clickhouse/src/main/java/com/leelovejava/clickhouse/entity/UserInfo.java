package com.leelovejava.clickhouse.entity;

import lombok.Data;

/**
 * @author leelovejava
 */
@Data
public class UserInfo {

    private Integer id ;
    private String userName ;
    private String passWord ;
    private String phone ;
    private String email ;
    private String createDay ;
}