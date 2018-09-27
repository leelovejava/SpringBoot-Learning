package com.imooc.bigdata.phoenix.mybatis.test.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.imooc.bigdata.phoenix.mybatis.test.UserInfo;

/**
 * Created by jixin on 18-3-11.
 */
@Mapper
public interface UserInfoMapper {

  @Insert("upsert into USER_INFO (ID,NAME) VALUES (#{user.id},#{user.name})")
  public void addUser(@Param("user") UserInfo userInfo);

  @Delete("delete from USER_INFO WHERE ID=#{userId}")
  public void deleteUser(@Param("userId") int userId);

  @Select("select * from USER_INFO WHERE ID=#{userId}")
  @ResultMap("userResultMap")
  public UserInfo getUserById(@Param("userId") int userId);

  @Select("select * from USER_INFO WHERE NAME=#{userName}")
  @ResultMap("userResultMap")
  public UserInfo getUserByName(@Param("userName") String userName);

  @Select("select * from USER_INFO")
  @ResultMap("userResultMap")
  public List<UserInfo> getUsers();
}
