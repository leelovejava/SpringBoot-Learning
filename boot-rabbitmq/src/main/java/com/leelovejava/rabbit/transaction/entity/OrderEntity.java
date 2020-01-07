package com.leelovejava.rabbit.transaction.entity;

import lombok.Data;

/**
 * 订单对象
 * @author 翟永超
 */
@Data
public class OrderEntity {

    private Long id;
    /**
     * 订单名称
     */
    private String name;
    /**
     * 下单金额
     */
    private Double orderMoney;
    /**
     * 订单id
     */
    private String orderId;
}