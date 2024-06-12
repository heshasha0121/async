package com.lvchuan.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lvchuan
 * @description feign开发环境动态修改访问地址
 * @date 2023/5/8 09:09:07
 */
@PropertySource(value = "classpath:/feign-dev.properties")
@ConfigurationProperties(prefix = "feign")
@Component
@Slf4j
public class FeignClientsBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private Map<String,String> urlMap;
    public Map<String,String> getUrlMap(){
        return this.urlMap;
    }
    public void setUrlMap(Map<String,String> urlMap){
        this.urlMap = urlMap;
    }
    private ApplicationContext applicationContext;
    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        if("true".equals(System.getenv("FEIGN_DEV"))
                && atomicInteger.getAndIncrement() == 0
                && !CollectionUtils.isEmpty(urlMap)) {
            log.info("本地url===={}", JSONObject.toJSONString(urlMap));
            String beanNameOfFeignClientFactoryBean = "org.springframework.cloud.openfeign.FeignClientFactoryBean";
            try {
                Class beanNameClz = Class.forName(beanNameOfFeignClientFactoryBean);
                applicationContext.getBeansOfType(beanNameClz)
                        .forEach((feignBeanName, beanOfFeignClientFactoryBean) -> {
                            try {
                                setField(beanNameClz, beanOfFeignClientFactoryBean);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return bean;
    }

    private  void setField(Class clazz, Object obj) throws Exception{

        Field field = ReflectionUtils.findField(clazz, "name");
        Field fieldUrl = ReflectionUtils.findField(clazz, "url");
        ReflectionUtils.makeAccessible(fieldUrl);
        if(Objects.nonNull(field)){
            ReflectionUtils.makeAccessible(field);
            Object value = field.get(obj);
            if(Objects.nonNull(value) && urlMap.get(value) != null){
                ReflectionUtils.setField(fieldUrl, obj, urlMap.get(value));
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

