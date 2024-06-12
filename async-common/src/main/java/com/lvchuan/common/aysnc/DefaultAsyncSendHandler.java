package com.lvchuan.common.aysnc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 默认实现
 * @author: lvchuan
 * @createTime: 2024-03-06 16:42
 */
@Slf4j
public class DefaultAsyncSendHandler implements AsyncSendHandler {
    @Override
    public void sendMsg(AsyncMsgDTO asyncMsgDTO) {
        log.info("发送消息处理类");
    }
}
