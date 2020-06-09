package com.leelovejava.clickhouse.mapper;

import com.leelovejava.clickhouse.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leelovejava
 */
public interface UserInfoMapper {
  /**
   * 写入数据
   * @param userInfo
   */
  void saveData (UserInfo userInfo) ;

  /**
   * ID 查询
   * @param id
   * @return
   */
  UserInfo selectById (@Param("id") Integer id) ;

  /**
   * 查询全部
   * @return
   */
  List<UserInfo> selectList () ;
}