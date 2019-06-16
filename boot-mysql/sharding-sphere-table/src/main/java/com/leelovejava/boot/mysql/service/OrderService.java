package com.leelovejava.boot.mysql.service;

import com.leelovejava.boot.mysql.mapper.OrderMapper;
import com.leelovejava.boot.mysql.model.OrderModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderService {
    @Resource
    private OrderMapper orderMapper;

    public int save(OrderModel orderModel) {
        return orderMapper.save(orderModel);
    }
}
