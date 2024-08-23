package com.lvchuan.common.canal.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.client.AbstractCanalClient;
import top.javatool.canal.client.handler.MessageHandler;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @description: rocketmq
 * @author: lvchuan
 * @createTime: 2024-08-22 17:22
 */
@Component
@Slf4j
@RefreshScope
public class RocketCanalClient extends AbstractCanalClient {
    @Autowired
    private MessageHandler messageHandler;

    @Value("${aliyun.rocketmq.accessKey}")
    private String accessKey;
    @Value("${aliyun.rocketmq.secretKey}")
    private String secretKey;
    @Value("${aliyun.rocketmq.nameSrvAddr}")
    private String nameSrvAddr;
    @Value("${aliyun.rocketmq.topic}")
    private String topic;
    @Value("${aliyun.rocketmq.canalGroup}")
    private String canalGroup;
    @Value("${aliyun.rocketmq.canalTag}")
    private String canalTag;

    @Override
    @PostConstruct
    public void process() {
        log.info("启动阿里云MQ消费者");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.GROUP_ID, canalGroup);
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, nameSrvAddr);
        Consumer consumer = ONSFactory.createConsumer(properties);
        //订阅多个Tag。
        consumer.subscribe(topic, canalTag, (msg, context) -> {
            log.info("消息：{}", new String(msg.getBody()));
            try {
                FlatMessage flatMessage = JSON.parseObject(msg.getBody(), FlatMessage.class);
                this.messageHandler.handleMessage(flatMessage);
            } catch (Exception e) {
                throw e;
            }
            return Action.CommitMessage;
        });
        consumer.start();
    }
}
