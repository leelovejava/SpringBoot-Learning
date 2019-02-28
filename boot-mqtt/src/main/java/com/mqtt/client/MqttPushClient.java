package com.mqtt.client;


import com.mqtt.client.callback.PushCallback;
import com.mqtt.config.MqttConfig;
import com.mqtt.dto.PushPayload;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mqtt消息推送或订阅客户端
 * @author Administrator
 */
@Component
public class MqttPushClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MqttClient client;

    private static volatile MqttPushClient mqttPushClient = null;
    @Resource
    private MqttConfig mqttConfig;

    public static MqttPushClient getInstance() {

        if (null == mqttPushClient) {
            synchronized (MqttPushClient.class) {
                if (null == mqttPushClient) {
                    mqttPushClient = new MqttPushClient();
                }
            }

        }
        return mqttPushClient;

    }

    private MqttPushClient() {
        connect();
    }

    private String username="admin";
    private String password="password";
    private String hostUrl="tcp://127.0.0.1:61613";
    private String clientId="mqttId";
    private String defaultTopic="topic";
    private Integer timeOut=10;
    private Integer keepAlive=20;

    private void connect() {
        try {
            // 创建MqttClient对象
            client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeOut);
            options.setKeepAliveInterval(keepAlive);
            try {
                //MqttClient绑定
                client.setCallback(new PushCallback());
                //MqttClient连接
                client.connect(options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param topic
     * @param pushMessage
     */
    public void publish(String topic, PushPayload pushMessage) {
        publish(0, false, topic, pushMessage);
    }

    /**
     * 发布
     *
     * @param qos
     * @param retained
     * @param topic
     * @param pushMessage
     */
    public void publish(int qos, boolean retained, String topic, PushPayload pushMessage) {
        // 创建MQTT的消息体
        MqttMessage message = new MqttMessage();
        // 设置消息传输的类型
        message.setQos(qos);
        // 设置是否在服务器中保存消息体
        message.setRetained(retained);
        // 设置消息的内容
        message.setPayload(pushMessage.toString().getBytes());
        // 创建MQTT相关的主题
        MqttTopic mTopic = client.getTopic(topic);
        if (null == mTopic) {
            logger.error("topic not exist");
        }
        MqttDeliveryToken token;
        try {
            //发送消息并获取回执
            token = mTopic.publish(message);
            token.waitForCompletion();
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅某个主题，qos默认为0
     *
     * @param topic
     */
    public void subscribe(String topic) {
        subscribe(topic, 0);
    }

    /**
     * 订阅某个主题
     *
     * @param topic
     * @param qos
     */
    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        String kdTopic = "good";
        PushPayload pushMessage = PushPayload.getPushPayloadBuider().setMobile("15345715326")
                .setContent("designModel")
                .bulid();
        MqttPushClient.getInstance().publish(0, false, kdTopic, pushMessage);
    }
}