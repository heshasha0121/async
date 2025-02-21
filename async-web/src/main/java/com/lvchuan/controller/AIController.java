package com.lvchuan.controller;

import com.lvchuan.model.AIChatReq;
import com.lvchuan.model.ai.deepseek.DeepSeekBeanOutputConverter;
import com.lvchuan.model.ai.deepseek.DeepSeekResponse;
import com.lvchuan.service.ai.IAIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @description: ai api接口
 * @author: lvchuan
 * @createTime: 2025-01-21 10:10
 */
@RestController
@RequestMapping("/ai")
public class AIController {
    @Autowired
    private IAIService aiService;

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/chasMsg")
    public String chasMsg(@RequestBody @Validated AIChatReq data) {
        return aiService.chasMsg(data);
    }

    //聊天
    @PostMapping("/chat")
    public DeepSeekResponse chat(@RequestBody AIChatReq param){
        //直接返回
        DeepSeekBeanOutputConverter deepSeekBeanOutputConverter = new DeepSeekBeanOutputConverter<>(DeepSeekResponse.class);
        return (DeepSeekResponse) chatClient.prompt(param.getMsg())
                .call()
                .entity(deepSeekBeanOutputConverter);
    }
}
