package com.lvchuan.controller;

import com.lvchuan.model.AIChatReq;
import com.lvchuan.service.ai.DeepSeekAppService;
import com.lvchuan.service.ai.IAIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public String chat(@RequestBody AIChatReq param){
        //直接返回
        return chatClient.prompt(param.getMsg()).call().content();
    }
}
