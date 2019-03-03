package com.mqtt.client.callback;

import com.mqtt.client.MqttPushClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * mqtt推送回调类
 *
 * @author Administrator
 * @date 2018/08/30 9:20
 */
public class PushCallback implements MqttCallback {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private MqttPushClient mqttPushClient;

    // SpringBoot成长笔记（九）集成MQTT `https://blog.csdn.net/mhm52368/article/details/84066173`

    /**
     * 掉线
     * MQTT断链重连后重复接收到最后一条消息 `https://blog.csdn.net/keybersan/article/details/83068525`
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        logger.info("[MQTT] 连接断开，30S之后尝试重连...");

        while (true) {
            try {
                Thread.sleep(30000);
                mqttPushClient.reConnect();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
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