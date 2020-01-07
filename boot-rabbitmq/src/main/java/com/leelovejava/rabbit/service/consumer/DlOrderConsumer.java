package com.leelovejava.rabbit.service.consumer;

import com.leelovejava.rabbit.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * @author 翟永超
 * @date 2019-06-28
 * @description 订单监听
 */
@Component
@Slf4j
public class DlOrderConsumer {


    @RabbitListener(queuesToDeclare = @Queue(RabbitConfig.ORDER_DL_QUEUE))
    public void HandlerMessage(Channel channel, Message message,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info(new String(message.getBody()));
        // 人工处理死信队列中的消息
        handlerDl(new String(message.getBody()));
        channel.basicAck(tag, false);
    }


    private void handlerDl(String message) {
        log.info("发送邮件，请求人工干预:{}", message);
    }
}