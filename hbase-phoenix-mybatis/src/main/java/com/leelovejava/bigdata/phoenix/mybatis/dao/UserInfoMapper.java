package com.leelovejava.bigdata.phoenix.mybatis.dao;

import java.util.List;

import com.leelovejava.bigdata.phoenix.mybatis.entity.UserInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

/**
 * Created by jixin on 18-3-11.
 */
@Mapper
public interface UserInfoMapper {

    /**
     * 插入
     * 存在id,即更新
     * @param userInfo
     */
    @Insert("upsert into USER_INFO (ID,NAME) VALUES (#{user.id},#{user.name})")
    void addUser(@Param("user") UserInfo userInfo);

    /**
     * 根据id删除
     *
     * @param userId
     */
    @Delete("delete from USER_INFO WHERE ID=#{userId}")
    void deleteUser(@Param("userId") int userId);

    /**
     * 根据id查询用户
     *
     * @param userId
     * @return
     */
    @Select("select * from USER_INFO WHERE ID=#{userId}")
    @ResultMap("userResultMap")
    UserInfo getUserById(@Param("userId") int userId);

    /**
     * 根据name查询
     *
     * @param userName
     * @return
     */
    @Select("select * from USER_INFO WHERE NAME=#{userName}")
    @ResultMap("userResultMap")
    UserInfo getUserByName(@Param("userName") String userName);

    /**
     * 查询全部的用户信息
     *
     * @return
     */
    @Select("select * from USER_INFO")
    @ResultMap("userResultMap")
    List<UserInfo> getUsers();

    /**
     * 分页查询用户信息
     *
     * @param page     页码
     * @param pageSize 每页显示的记录数
     * @return
     */
    @Select("select * from USER_INFO limit #{pageSize} OFFSET #{page}")
    @ResultMap("userResultMap")
    List<UserInfo> getUsersByPage(@Param("page") int page, @Param("pageSize") int pageSize);
}
