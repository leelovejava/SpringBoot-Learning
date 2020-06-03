package com.leelovejava.boot.statemachine.service;

import com.leelovejava.boot.statemachine.constant.RedisKeyConstant;
import com.leelovejava.boot.statemachine.entity.Events;
import com.leelovejava.boot.statemachine.entity.Order;
import com.leelovejava.boot.statemachine.entity.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 状态机发送逻辑类
 *
 * @author tianhao
 */
@Service
@Slf4j
public class StateMachineService {


    @Resource(name = "redisStateMachinePersister")
    private StateMachinePersister<States, Events, String> redisStateMachinePersister;

    @Autowired
    private StateMachineFactory<States, Events> redisStateMachineContextRepository;

    public void execute(Order order, Events event) {
        // 利用随机ID创建状态机，创建时没有与具体定义状态机绑定
        String businessId = order.getId().toString();
        StateMachine<States, Events> stateMachine = redisStateMachineContextRepository.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        try {
            // 第一次无需恢复状态机
            if (!event.equals(Events.PAY)) {
                redisStateMachinePersister.restore(stateMachine, RedisKeyConstant.STATE_MACHINE_PREFIX + businessId);
            }
            // 发送事件，返回是否执行成功
            boolean success = stateMachine.sendEvent(MessageBuilder
                    .withPayload(event)
                    .setHeader("order", order)
                    .build());

            if (success) {
                redisStateMachinePersister.persist(stateMachine, RedisKeyConstant.STATE_MACHINE_PREFIX + businessId);
            } else {
                log.error("状态机处理未执行成功，请处理，ID：{}", businessId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stateMachine.stop();
        }
    }
}
