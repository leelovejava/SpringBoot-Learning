package com.mqtt.client.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mqtt推送回调类
 *
 * @author Administrator
 * @date 2018/08/30 9:20
 */
public class PushCallback implements MqttCallback {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        logger.error("连接断开，可以做重连");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        logger.error("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        logger.error("接收消息主题 : " + topic);
        logger.error("接收消息Qos : " + message.getQos());
        logger.error("接收消息内容 : " + new String(message.getPayload()));
    }
}