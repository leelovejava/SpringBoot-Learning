package com.leelovejava.boot.statemachine;

import com.leelovejava.boot.statemachine.entity.Events;
import com.leelovejava.boot.statemachine.entity.Order;
import com.leelovejava.boot.statemachine.service.StateMachineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author leelovejava
 */
@SpringBootApplication
public class StateMachineApplication implements CommandLineRunner {
    @Resource
    private StateMachineService stateMachineService;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApplication.class, args);
    }

    @Override
    public void run(String... args) {

        stateMachineService.execute(new Order(1, "547568678", "广东省深圳市", "13435465465", "PAY"), Events.PAY);
        stateMachineService.execute(new Order(1, "547568678", "广东省深圳市", "13435465465", "RECEIVE"), Events.RECEIVE);
    }

}
