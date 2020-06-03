# 状态机

## 描述
订单状态机,多个同时存在,使用redis持久化

## doc

[官网](https://spring.io/projects/spring-statemachine)

[spring statemachine的企业可用级开发指南5-传递参数的message](https://my.oschina.net/u/173343/blog/3045884)

[demo](https://gitee.com/wphmoon/statemachine/)



[Spring Statemachine 概念及应用](https://www.jianshu.com/p/9ee887e045dd)

[持久化](https://my.oschina.net/u/173343/blog/3047160)

## 代码

### 监听器

```java
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {
    /**
     * 为当前的状态机指定了状态监听器，其中listener()则是调用了下一个内容创建的监听器实例，用来处理各个各个发生的状态迁移事件
     *
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
                .withConfiguration()
                // 指定状态机的处理监听器
                .listener(listener());
    }
    
    /**
     * 创建StateMachineListener状态监听器的实例，在该实例中会定义具体的状态迁移处理逻辑，
     * 上面的实现中只是做了一些输出，实际业务场景会会有更负责的逻辑，所以通常情况下，我们可以将该实例的定义放到独立的类定义中，并用注入的方式加载进来
     *
     * @return
     */
   @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {

            @Override
            public void transition(Transition<States, Events> transition) {
                if (transition.getTarget().getId() == States.UNPAID) {
                    logger.info("订单创建，待支付");
                    return;
                }

                if (transition.getSource().getId() == States.UNPAID
                        && transition.getTarget().getId() == States.WAITING_FOR_RECEIVE) {
                    logger.info("用户完成支付，待收货");
                    return;
                }

                if (transition.getSource().getId() == States.WAITING_FOR_RECEIVE
                        && transition.getTarget().getId() == States.DONE) {
                    logger.info("用户已收货，订单完成");
                    return;
                }
            }

        };
    }
}
```

#### 注解方式

```java
@WithStateMachine
public class EventListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @OnTransition(target = "UNPAID")
    public void create() {
        logger.info("订单创建，待支付");
    }

    @OnTransition(source = "UNPAID", target = "WAITING_FOR_RECEIVE")
    public void pay() {
        logger.info("用户完成支付，待收货");
    }

    @OnTransitionStart(source = "UNPAID", target = "WAITING_FOR_RECEIVE")
    public void payStart(Message<Events> message) {
        System.out.println("传递的参数：" + message.getHeaders().get("order"));
        logger.info("用户完成支付，待收货: start");
    }

    @OnTransitionEnd(source = "UNPAID", target = "WAITING_FOR_RECEIVE")
    public void payEnd() {
        logger.info("用户完成支付，待收货: end");
    }

    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "DONE")
    public void receive() {
        logger.info("用户已收货，订单完成");
    }

}
```
