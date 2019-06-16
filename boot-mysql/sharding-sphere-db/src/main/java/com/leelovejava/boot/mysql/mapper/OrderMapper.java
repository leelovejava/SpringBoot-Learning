package com.leelovejava.boot.mysql.mapper;

import com.leelovejava.boot.mysql.model.OrderModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    int save(OrderModel orderModel);
}
