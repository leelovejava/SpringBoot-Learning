package com.leelovejava.clickhouse.service;


import com.leelovejava.clickhouse.entity.UserInfo;

import java.util.List;

/**
 * @author leelovejava
 */
public interface UserInfoService {
    /**
     * 写入数据
     *
     * @param userInfo
     */
    void saveData(UserInfo userInfo);

    /**
     * ID 查询
     *
     * @param id
     * @return
     */
    UserInfo selectById(Integer id);

    /**
     * 查询全部
     *
     * @return
     */
    List<UserInfo> selectList();
}