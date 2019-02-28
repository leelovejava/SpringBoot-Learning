package com.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;



/**
 * @author Administrator
 */
@SpringBootApplication
@AutoConfigurationPackage
public class MqttApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MqttApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MqttApplication.class, args);
    }
}
