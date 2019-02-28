package com.mqtt.test;


import com.mqtt.MqttApplication;
import com.mqtt.client.MqttPushClient;
import com.mqtt.dto.PushPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = MqttApplication.class)
public class test {
    @Resource
    private MqttPushClient mqttClientComponent;

    @Test
    public void test() {

        PushPayload pushPayload = PushPayload.getPushPayloadBuider().setContent("test")
                .setMobile("119")
                .setType("2018")
                .bulid();
        mqttClientComponent.publish("publish", pushPayload);

    }
}
