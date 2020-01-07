package com.leelovejava.rabbit.transaction.mapper;

import com.leelovejava.rabbit.transaction.entity.DispatchEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 派单
 *
 * @author 翟永超
 */
@Mapper
public interface DispatchMapper {

    /**
     * 新增派单任务
     *
     * @param distributeEntity
     * @return int
     */
    @Insert("INSERT into platoon values (null,#{orderId},#{takeoutUserId});")
    int insertDistribute(DispatchEntity distributeEntity);

}