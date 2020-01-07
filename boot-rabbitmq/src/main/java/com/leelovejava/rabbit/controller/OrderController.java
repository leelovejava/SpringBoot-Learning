package com.leelovejava.rabbit.controller;

import com.leelovejava.rabbit.service.OrderService;
import com.leelovejava.rabbit.service.BaseApiService;
import com.leelovejava.rabbit.transaction.ResponseBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单
 *
 * @author 翟永超
 */
@RestController
public class OrderController extends BaseApiService {
    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @return
     */
    @RequestMapping("/addOrder")
    public ResponseBase addOrder() {
        return orderService.addOrderAndDispatch();
    }

}