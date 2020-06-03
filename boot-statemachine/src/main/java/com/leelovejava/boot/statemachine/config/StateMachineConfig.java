package com.leelovejava.boot.statemachine.config;


import com.leelovejava.boot.statemachine.entity.Events;
import com.leelovejava.boot.statemachine.entity.States;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;

import javax.annotation.Resource;
import java.util.EnumSet;


/**
 * 状态机配置
 * `@EnableStateMachine` 注解用来启用Spring StateMachine状态机功能
 *
 * @author leelovejava
 */
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {


    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    //@Resource
    //private StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister;


    /**
     * 初始化当前状态机拥有哪些状态
     * initial(States.UNPAID)定义了初始状态为UNPAID
     *
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        // 定义状态机中的状态
        states
                .withStates()
                // 初始状态
                .initial(States.UNPAID)
                .states(EnumSet.allOf(States.class));
    }

    /**
     * 初始化当前状态机有哪些状态迁移动作，
     * 其中命名中我们很容易理解每一个迁移动作，都有来源状态source，目标状态target以及触发事件event
     *
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
                .withExternal()
                // 指定状态来源和目标
                .source(States.UNPAID).target(States.WAITING_FOR_RECEIVE)
                // 指定触发事件
                .event(Events.PAY)
                .and()
                .withExternal()
                .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                .event(Events.RECEIVE);
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config.withConfiguration()
                .machineId("orderStateMachine");

        /*config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);*/
    }

    @Bean
    public RedisStateMachineContextRepository redisStateMachineContextRepository(){
        return new RedisStateMachineContextRepository<States, Events>(redisConnectionFactory);
    }

    /**
     * 通过redisConnectionFactory创建StateMachinePersist
     *
     * @return
     */
    @Bean
    public StateMachinePersist<States, Events, String> stateMachinePersist() {
        return new RepositoryStateMachinePersist<States, Events>(redisStateMachineContextRepository());
    }

    /**
     * 注入RedisStateMachinePersister对象
     *
     * @return
     */
    @Bean
    public RedisStateMachinePersister<States, Events> redisStateMachinePersister(
            StateMachinePersist<States, Events, String> stateMachinePersist) {
        return new RedisStateMachinePersister<States, Events>(stateMachinePersist);
    }

    /*@Bean
    public StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister(
            RedisStateMachineRepository jpaStateMachineRepository) {
        return new RedisPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }*/
}
