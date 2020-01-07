package com.leelovejava.rabbit.transaction.entity;

import lombok.Data;

/**
 * 派单
 *
 * @author 翟永超
 */
@Data
public class DispatchEntity {

    private Long id;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 外卖员id
     */
    private Long takeoutUserId;

}