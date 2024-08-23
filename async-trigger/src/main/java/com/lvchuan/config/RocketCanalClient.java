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
import top.javatool.canal.client.handler.MessageHandler;

/**
 * @description: rocketmq
 * @author: lvchuan
 * @createTime: 2024-08-22 17:22
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = MqTopicConstant.canal_topic, consumerGroup = MqTopicConstant.canal_group, selectorExpression = MqTopicConstant.canal_tag)
public class RocketCanalClient implements RocketMQListener<MessageExt> {
    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String msg = new String(body);
        log.info("[{}]监听到消息：msg:{}", messageExt.getMsgId(), msg);
        if (StrUtil.isNotBlank(msg)) {
            AsyncMsgDTO data = JSON.parseObject(msg, AsyncMsgDTO.class);
            messageHandler.handleMessage(data);
        }
    }
}
