package com.lvchuan.common;

import com.alibaba.fastjson.JSON;
import com.lvchuan.common.aysnc.AsyncMsgDTO;
import com.lvchuan.common.aysnc.AsyncSendHandler;
import com.lvchuan.mq.MQProducerService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 公共消息发送服务
 * @author: lvchuan
 * @createTime: 2024-06-12 15:25
 */
@Component
public class CommonAsyncSendHandler implements AsyncSendHandler {
    @Autowired
    private MQProducerService mqProducerService;

    @Override
    public void sendMsg(AsyncMsgDTO asyncMsgDTO) {
        mqProducerService.sendCommon(JSON.toJSONString(asyncMsgDTO));
    }
}
