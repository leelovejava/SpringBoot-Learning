package com.leelovejava.rabbit.service.consumer;

import com.alibaba.fastjson.JSONObject;
import com.leelovejava.rabbit.config.RabbitConfig;
import com.leelovejava.rabbit.transaction.entity.DispatchEntity;
import com.leelovejava.rabbit.transaction.mapper.DispatchMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 派单服务 消费者
 *
 * @author 翟永超
 */
@Component
@Slf4j
public class DispatchConsumer {
    @Resource
    private DispatchMapper dispatchMapper;

    @RabbitListener(queuesToDeclare = @Queue(RabbitConfig.ORDER_DIC_QUEUE))
    public void process(Message message, @Header(AmqpHeaders.REDELIVERED) boolean reDelivered, Channel channel) throws Exception {
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        log.debug("派单服务平台:{},消息id:", msg, messageId);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String orderId = jsonObject.getString("orderId");
        if (StringUtils.isEmpty(orderId)) {
            // 日志记录
            return;
        }
        // redis处理幂等性
        DispatchEntity dispatchEntity = new DispatchEntity();
        // 订单id
        dispatchEntity.setOrderId(orderId);
        // 外卖员id
        dispatchEntity.setTakeoutUserId(12L);

        try {
            int insertDistribute = dispatchMapper.insertDistribute(dispatchEntity);
            if (insertDistribute > 0) {
                // 手动签收消息,通知mq服务器端删除该消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            log.error("消息处理失败", e);
            if (reDelivered) {
                log.debug("消息已重复处理失败：{}", message);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("消息处理失败", e);
                // 重新入队一次
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                // 丢弃该消息
                ///channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

}