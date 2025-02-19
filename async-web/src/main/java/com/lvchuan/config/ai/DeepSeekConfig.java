package com.lvchuan.config.ai;

import org.springframework.ai.autoconfigure.ollama.OllamaConnectionDetails;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

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

    private final ChatMemory chatMemory = new InMemoryChatMemory();

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(this.model).defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();
    }


}
