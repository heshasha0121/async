package com.lvchuan.common.aysnc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvchuan
 * @description 异步方法配置
 * @date 2023/5/23 09:25:06
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "async.handle", havingValue = "true", matchIfMissing = true)
public class AsyncHandlerConfig implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> asyncClass = this.applicationContext.getBeansWithAnnotation(AsyncEnable.class);
        for (Map.Entry<String, Object> entry : asyncClass.entrySet()) {
            Class clazz = entry.getValue().getClass();
            String className = clazz.getName();
            if (className.contains("$$")) {
                //处理代理类
                Object object = AopProxyUtils.getSingletonTarget(entry.getValue());
                clazz = object.getClass();
                className = clazz.getName();
            }
            Method[] methods = clazz.getDeclaredMethods();
            List<String> methodList = new ArrayList<>();
            Map<String, AsyncDTO> methodDtoMap = new HashMap<>();
            for (Method method: methods) {
                if (!method.isAnnotationPresent(AsyncEnable.class)) {
                    continue;
                }
                AsyncEnable asyncEnable = method.getAnnotation(AsyncEnable.class);
                methodList.add(method.getName());
                AsyncDTO asyncDTO = new AsyncDTO();
                asyncDTO.setBeanName(entry.getKey());
                asyncDTO.setValue(asyncEnable.value());
                methodDtoMap.put(method.getName(), asyncDTO);
            }
            AsyncProxyUtil.asyncMethod.put(className, methodDtoMap);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncSendHandler asyncHandler() {
        return new DefaultAsyncSendHandler();
    }

    @Bean
    public AsyncProxyUtil asyncProxyUtil() {
        return new AsyncProxyUtil();
    }
}
