package com.lvchuan.service;

import com.lvchuan.common.aysnc.AsyncHandler;
import com.lvchuan.common.aysnc.AsyncMsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description: 消息处理
 * @author: lvchuan
 * @createTime: 2024-03-06 16:27
 */
@Slf4j
@Component
public class MsgService extends AsyncHandler {

    @Override
    public void sendMsg(AsyncMsgDTO asyncMsgDTO) {
        log.info("业务执行发送mq消息");
        this.receiveMsg(asyncMsgDTO);
    }

    public void receiveMsg(AsyncMsgDTO asyncMsgDTO) {
        this.handleMsg(asyncMsgDTO);
    }
}
