package com.lvchuan.config;

import com.lvchuan.common.aysnc.AsyncAcceptHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 异步消费配置
 * @author: lvchuan
 * @createTime: 2024-06-12 15:58
 */
@Configuration
public class AsyncAcceptHandlerConfig {
    @Bean
    public AsyncAcceptHandler asyncAcceptHandler() {
        return new AsyncAcceptHandler();
    }
}
