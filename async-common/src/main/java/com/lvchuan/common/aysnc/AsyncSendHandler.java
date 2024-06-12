package com.lvchuan.common.aysnc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

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
