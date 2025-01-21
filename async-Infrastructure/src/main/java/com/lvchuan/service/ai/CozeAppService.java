package com.lvchuan.service.ai;

import com.alibaba.fastjson.JSON;
import com.lvchuan.model.AIChatReq;
import com.lvchuan.model.ai.coze.AdditionalMessages;
import com.lvchuan.model.ai.coze.Body;
import com.lvchuan.utils.OkhttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: coze ai实现
 * @author: lvchuan
 * @createTime: 2025-01-21 9:50
 */
@Component
@Slf4j
public class CozeAppService implements IAIService{
    @Resource
    private OkhttpUtils okhttpUtils;

    private static final String chat_url = "https://api.coze.cn/v3/chat";

    private static final String head = "Bearer pat_WKw2x0mvAy1Ls3vpXxHs8RFL6kIKcTtAHfbJZSiWlw48JkUz12xb34rwyF10USxi";

    private static final String bot_id = "7461915930403127307";

    private static final String user_id = "123";

    @Override
    public String chasMsg(AIChatReq msg) {
        Body body = new Body();
        body.setBot_id(bot_id);
        body.setUser_id(user_id);
        List<AdditionalMessages> additional_messages = new ArrayList<>();
        AdditionalMessages additionalMessages = new AdditionalMessages();
        additionalMessages.setContent(msg.getMsg());
        additional_messages.add(additionalMessages);
        body.setAdditional_messages(additional_messages);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Authorization", head);
        return okhttpUtils.post(chat_url+ "?conversation_id=7462194714574438434", JSON.toJSONString(body), headMap);
    }
}
