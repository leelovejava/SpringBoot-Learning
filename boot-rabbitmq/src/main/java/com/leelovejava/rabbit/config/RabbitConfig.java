package com.leelovejava.rabbit.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 翟永超
 * @create 2016/9/25.
 * @blog http://blog.didispace.com
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue helloQueue() {
        return new Queue("hello");
    }

    // rabbit之分布式事务的配置//

    /**
     * 下单并且派单存队列
     */
    public static final String ORDER_DIC_QUEUE = "order_dic_queue";
    /**
     * 补单队列，判断订单是否已经被创建
     */
    public static final String ORDER_CREATE_QUEUE = "order_create_queue";
    /**
     * 死信队列
     */
    public static final String ORDER_DL_QUEUE = "order_dl_queue";
    /**
     * 下单并且派单交换机
     */
    private static final String ORDER_EXCHANGE_NAME = "order_exchange_name";

    /**
     * 1.定义派单队列
     *
     * @return
     */
    @Bean
    public Queue directOrderDicQueue() {
        return new Queue(ORDER_DIC_QUEUE);
    }

    /**
     * 2.定义补订单队列
     *
     * @return
     */
    @Bean
    public Queue directCreateOrderQueue() {
        ///return new Queue(ORDER_CREATE_QUEUE);
        return QueueBuilder.durable(ORDER_CREATE_QUEUE)
                //配置死信
                .withArgument("x-dead-letter-exchange","dlExchange")
                .withArgument("x-dead-letter-routing-key","orderRoutingKey")
                .build();
    }


    /**
     * 3. 定义死信队列
     *
     * @return
     */
    @Bean
    public Queue dlQueue() {
        return QueueBuilder.durable(ORDER_DL_QUEUE)
                .build();
    }

    /**
     * 2.定义交换机
     *
     * @return
     */
    @Bean
    public DirectExchange directOrderExchange() {
        return new DirectExchange(ORDER_EXCHANGE_NAME);
    }

    /**
     * 死信队列的交换机
     *
     * @return
     */
    @Bean
    public DirectExchange dlExchange() {
        return (DirectExchange) ExchangeBuilder.directExchange("dlExchange").build();
    }

    /**
     * 3.派单单队列与交换机绑定
     *
     * @return
     */
    @Bean
    public Binding bindingExchangeDirectOrderDicQueue() {
        return BindingBuilder.
                bind(directOrderDicQueue()).
                to(directOrderExchange()).
                with("orderRoutingKey");
    }


    /**
     * 4.补单队列与交换机绑定
     *
     * @return
     */
    @Bean
    public Binding bindingExchangeCreateOrder() {
        return BindingBuilder.
                bind(directCreateOrderQueue()).
                to(directOrderExchange()).
                with("orderRoutingKey");
    }

    @Bean
    public Binding dlMessageBinding() {
        return BindingBuilder.bind(dlQueue()).to(dlExchange()).with("orderRoutingKey");
    }

}
