package com.leelovejava.boot.datasource.mycat.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户 数据实体层
 * @author leelovejava
 */
@Entity
@Table(name = "tb_user")
@Data
public class UserModel {

    @Id
    private Long id;

    private String name;
}
