package com.lvchuan.common.aysnc;

/**
 * @description: 异步处理实现接口
 * @author: lvchuan
 * @createTime: 2024-03-06 16:02
 */
public interface AsyncSendHandler {

    /**
     * 发送消息
     * @param asyncMsgDTO 解析后的参数消息
     * @return 是否发送成功
     */
    void sendMsg(AsyncMsgDTO asyncMsgDTO);
}
