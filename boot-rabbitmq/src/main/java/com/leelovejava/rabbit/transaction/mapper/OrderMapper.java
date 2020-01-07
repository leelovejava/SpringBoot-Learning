package com.leelovejava.rabbit.transaction.mapper;

import com.leelovejava.rabbit.transaction.entity.OrderEntity;
import org.apache.ibatis.annotations.*;


/**
 * @author 翟永超
 */
@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     *
     * @param orderEntity
     * @return
     */
    @Insert(value = "INSERT INTO `order_info` VALUES (#{id}, #{name}, #{orderMoney},#{orderId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addOrder(OrderEntity orderEntity);

    /**
     * 根据订单id查询
     *
     * @param orderId
     * @return
     */
    @Select("SELECT id as id ,name as name , order_money as orderMoney,orderId as orderId from order_info where orderId=#{orderId};")
    OrderEntity findOrderId(@Param("orderId") String orderId);

}