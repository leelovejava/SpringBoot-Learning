package com.leelovejava.rabbit.service;

import com.alibaba.fastjson.JSONObject;
import com.leelovejava.rabbit.transaction.ResponseBase;
import com.leelovejava.rabbit.transaction.entity.OrderEntity;
import com.leelovejava.rabbit.transaction.mapper.OrderMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 订单
 *
 * @author 翟永超
 */
@Service
public class OrderService extends BaseApiService implements RabbitTemplate.ConfirmCallback {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建订单和配送单
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseBase addOrderAndDispatch() {
        //先下单 订单表插入数据
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setName("黄焖鸡米饭");
        // 价格是300元
        orderEntity.setOrderMoney(300d);
        // 商品id
        String orderId = UUID.randomUUID().toString();
        orderEntity.setOrderId(orderId);
        // 1.先下单，创建订单 (往订单数据库中插入一条数据)
        int orderResult = orderMapper.addOrder(orderEntity);
        System.out.println("orderResult:" + orderResult);
        if (orderResult <= 0) {
            return setResultError("下单失败!");
        }
        // 2.订单表插插入完数据后 订单表发送 外卖小哥
        send(orderId);
        // 发生异常
        ///int i = 1 / 0;
        return setResultSuccess();
    }

    /**
     * 消息发送
     *
     * @param orderId
     */
    private void send(String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        String msg = jsonObject.toJSONString();
        System.out.println("msg:" + msg);
        // 封装消息
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8").setMessageId(orderId).build();
        // 构建回调返回的数据
        CorrelationData correlationData = new CorrelationData(orderId);
        // 发送消息
        this.rabbitTemplate.setMandatory(true);
        // 回调方法  重试超过次数限制,进行人工补偿
        this.rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.convertAndSend("order_exchange_name", "orderRoutingKey", message, correlationData);

    }

    /**
     * 生产消息确认机制 生产者往服务器端发送消息的时候 采用应答机制
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // id 都是相同的  全局ID
        String orderId = correlationData.getId();
        System.out.println("消息id:" + correlationData.getId());
        // 消息发送成功
        if (ack) {
            System.out.println("消息发送确认成功");
        } else {
            // 重试机制
            send(orderId);
            System.out.println("消息发送确认失败:" + cause);
        }

    }

}