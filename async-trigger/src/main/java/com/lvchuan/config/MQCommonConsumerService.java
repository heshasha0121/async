package com.lvchuan.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.lvchuan.common.MqTopicConstant;
import com.lvchuan.common.aysnc.AsyncAcceptHandler;
import com.lvchuan.common.aysnc.AsyncMsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @description: mq公共消费者
 * @author: lvchuan
 * @createTime: 2024-06-12 14:21
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = MqTopicConstant.common_topic, consumerGroup = MqTopicConstant.common_group, selectorExpression = MqTopicConstant.common_tag)
public class MQCommonConsumerService implements RocketMQListener<MessageExt> {
    @Autowired
    private AsyncAcceptHandler asyncAcceptHandler;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String msg = new String(body);
        log.info("[{}]监听到消息：msg:{}", messageExt.getMsgId(), msg);
        if (StrUtil.isNotBlank(msg)) {
            AsyncMsgDTO data = JSON.parseObject(msg, AsyncMsgDTO.class);
            asyncAcceptHandler.handleMsg(data);
        }
    }
}
