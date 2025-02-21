package com.lvchuan.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: DeepSeek ai config
 * @author: lvchuan
 * @createTime: 2025-02-19 16:15
 */
@Configuration
public class DeepSeekConfig {
    //注入模型，配置文件中的模型，或者可以在方法中指定模型
    @Autowired
    private OllamaChatModel model;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(this.model).defaultAdvisors().build();
    }
}
