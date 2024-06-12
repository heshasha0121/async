package com.lvchuan.mq;

import com.alibaba.fastjson.JSON;
import com.lvchuan.common.MqTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @description: mq生产者
 * @author: lvchuan
 * @createTime: 2024-06-12 14:09
 */
@Component
@Slf4j
public class MQProducerService {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public SendResult sendCommon(String msg) {
        SendResult sendResult = rocketMQTemplate.syncSend(MqTopicConstant.common_topic + ":" + MqTopicConstant.common_tag, MessageBuilder.withPayload(msg).build());
        log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));
        return sendResult;
    }
}
