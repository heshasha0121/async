package com.lvchuan.config;

import com.lvchuan.common.canal.config.DataMapColumnModelFactory;
import com.lvchuan.common.canal.config.DataMapRowDataHandlerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.javatool.canal.client.handler.EntryHandler;
import top.javatool.canal.client.handler.MessageHandler;
import top.javatool.canal.client.handler.RowDataHandler;
import top.javatool.canal.client.handler.impl.AsyncFlatMessageHandlerImpl;
import top.javatool.canal.client.handler.impl.SyncFlatMessageHandlerImpl;
import top.javatool.canal.client.spring.boot.autoconfigure.ThreadPoolAutoConfiguration;
import top.javatool.canal.client.spring.boot.properties.CanalKafkaProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @description: roekctMQ canal配置类
 * @author: lvchuan
 * @createTime: 2024-08-23 9:45
 */
@Configuration
@EnableConfigurationProperties({CanalKafkaProperties.class})
@ConditionalOnBean({EntryHandler.class})
@ConditionalOnProperty(
        value = {"canal.mode"},
        havingValue = "rocketMQ"
)
@Import({ThreadPoolAutoConfiguration.class})
public class RocketMQClientAutoConfiguration {
    @Bean
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new DataMapRowDataHandlerImpl(new DataMapColumnModelFactory());
    }

    @Bean
    @ConditionalOnProperty(
            value = {"canal.async"},
            havingValue = "true",
            matchIfMissing = true
    )
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers, ExecutorService executorService) {
        return new AsyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(
            value = {"canal.async"},
            havingValue = "false"
    )
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers) {
        return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
    }
}
