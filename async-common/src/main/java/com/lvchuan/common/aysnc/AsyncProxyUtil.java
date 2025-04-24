package com.lvchuan.common.aysnc;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvchuan
 * @description 异步方法代理
 * @date 2023/5/23 10:47:50
 */
@Slf4j
public class AsyncProxyUtil implements MethodInterceptor {
    public static Map<String, Map<String, AsyncDTO>> asyncMethod = new HashMap<>();

    private static final ConcurrentHashMap<Class<?>, Enhancer> enhancerCache = new ConcurrentHashMap<>();

    @Autowired
    private AsyncSendHandler asyncSendHandler;
    @Autowired
    private ApplicationContext applicationContext;

    public <T> T proxy(T t) {
        Class targetClass = t.getClass();
        Enhancer enhancer = enhancerCache.computeIfAbsent(targetClass, clazz -> {
            Enhancer e = new Enhancer();
            e.setSuperclass(clazz);
            e.setCallback(this);
            return e;
        });
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Map<String, AsyncDTO> proxyMethodList = AsyncProxyUtil.asyncMethod.get(method.getDeclaringClass().getName());
        if (Objects.isNull(proxyMethodList) || CollectionUtils.isEmpty(proxyMethodList)) {
            return this.executeSpringMethod(method, objects);
        }
        if (Objects.isNull(proxyMethodList.get(method.getName()))) {
            return this.executeSpringMethod(method, objects);
        }
        Parameter[] parameters = method.getParameters();
        if (Objects.isNull(parameters) || parameters.length == 0) {
            throw new RuntimeException("["+ method.getDeclaringClass().getName() +"]方法["+ method.getName() +"] 缺少bizCode参数");
        }
        String bizCode = null;
        List<AsyncParamDTO> paramDTOList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            AsyncParamDTO asyncParamDTO = new AsyncParamDTO();
            if (Objects.isNull(objects[i])) {
                asyncParamDTO.setValue(null);
            } else {
                asyncParamDTO.setValue(objects[i]);
            }
            String type = parameters[i].getType().getName();
            String sourceType = objects[i].getClass().getName();
            asyncParamDTO.setType(parameters[i].getType().getName());
            if (!StrUtil.equals(type, sourceType)) {
                asyncParamDTO.setSourceType(objects[i].getClass().getName());
            }
            if (parameters[i].isAnnotationPresent(AsyncBizCode.class)) {
                bizCode = String.valueOf(objects[i]);
            }
            paramDTOList.add(asyncParamDTO);
        }
        if (StringUtils.isEmpty(bizCode)) {
            throw new RuntimeException("["+ method.getDeclaringClass().getName() +"]方法["+ method.getName() +"] 缺少bizCode参数");
        }
        AsyncDTO asyncDTO = proxyMethodList.get(method.getName());
        AsyncMsgDTO asyncMsgDTO = new AsyncMsgDTO();
        asyncMsgDTO.setValue(asyncDTO.getValue());
        asyncMsgDTO.setBeanName(asyncDTO.getBeanName());
        asyncMsgDTO.setBizCode(bizCode);
        asyncMsgDTO.setParamList(paramDTOList);
        asyncMsgDTO.setMethodName(method.getName());

        log.info("发送消息：{}", JSON.toJSONString(asyncMsgDTO));
        //消息发送
        asyncSendHandler.sendMsg(asyncMsgDTO);
        return null;
    }

    /**
     * 执行spring的方法，否则会执行本地方法
     */
    public Object executeSpringMethod(Method oldMethod,Object[] param) {
        Class[] classArr = new Class[param.length];
        if (param != null && param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                classArr[i] = param[i].getClass();
            }
        }
        Class clazz = oldMethod.getDeclaringClass();
        Object object = applicationContext.getBean(clazz);
        return ReflectionUtils.invokeMethod(oldMethod, object, param);
    }
}
