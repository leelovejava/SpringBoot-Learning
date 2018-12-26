package com.leelovejava.bigdata.phoenix.mybatis.entity;

/**
 * Created by jixin on 18-3-11.
 */
public class UserInfo {

  private int id;
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserInfo() {
  }

  public UserInfo(int id, String name) {
    this.id = id;
    this.name = name;
  }
}
