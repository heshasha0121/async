package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.aysnc.AsyncDTO;
import org.example.aysnc.AsyncEnable;
import org.example.aysnc.AsyncProxyUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
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
public class AsyncConfig implements CommandLineRunner {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> asyncClass = this.applicationContext.getBeansWithAnnotation(AsyncEnable.class);
        for (Map.Entry<String, Object> entry : asyncClass.entrySet()) {
            Class clazz = entry.getValue().getClass();
            Method[] methods = entry.getValue().getClass().getDeclaredMethods();
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
            AsyncProxyUtil.asyncMethod.put(clazz.getName(), methodDtoMap);
        }
    }
}
