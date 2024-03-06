package com.lvchuan.common.aysnc;


/**
 * @description: 默认实现
 * @author: lvchuan
 * @createTime: 2024-03-06 16:42
 */
public class DefaultAsyncHandler extends AsyncHandler {
    @Override
    public void sendMsg(AsyncMsgDTO asyncMsgDTO) {
        this.handleMsg(asyncMsgDTO);
    }
}
