package com.lvchuan.config;

import cn.hutool.core.collection.CollUtil;
import com.lvchuan.common.log.ILogDescHandler;
import com.lvchuan.common.log.LogFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 日志秒速配置类
 * @author: lvchuan
 * @createTime: 2024-10-15 15:00
 */
@Configuration
@Slf4j
public class LogDescHandleConfig implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        Map<String, ILogDescHandler> scanMap = this.applicationContext.getBeansOfType(ILogDescHandler.class);
        if (CollUtil.isEmpty(scanMap)) {
            LogFactory.logDescHandlerMap = new HashMap<>();
            return;
        }
        for (Map.Entry<String, ILogDescHandler> entry : scanMap.entrySet()) {
            ILogDescHandler service = entry.getValue();
            Class logDescClass = service.getClass();
            LogFactory.logDescHandlerMap.put(logDescClass.getName(), service);
        }
    }
}
