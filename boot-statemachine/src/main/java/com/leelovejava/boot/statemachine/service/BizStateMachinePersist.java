package com.leelovejava.boot.statemachine.service;

import com.leelovejava.boot.statemachine.entity.Events;
import com.leelovejava.boot.statemachine.entity.States;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持久化到本地内存
 *
 * @author leelovejava
 * @see 'https://my.oschina.net/u/173343/blog/3047160'
 */
@Component
public class BizStateMachinePersist implements StateMachinePersist<States, Events, Integer> {

    static Map<Integer, States> cache = new ConcurrentHashMap<>(16);

    @Override
    public void write(StateMachineContext<States, Events> stateMachineContext, Integer integer) throws Exception {
        cache.put(integer, stateMachineContext.getState());
    }

    @Override
    public StateMachineContext<States, Events> read(Integer integer) throws Exception {
        return cache.containsKey(integer) ?
                new DefaultStateMachineContext<>(cache.get(integer), null, null, null, null, "orderStateMachine") :
                new DefaultStateMachineContext<>(States.UNPAID, null, null, null, null, "orderStateMachine");
    }
}
