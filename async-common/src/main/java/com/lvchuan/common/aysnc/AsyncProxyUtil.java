package com.lvchuan.common.aysnc;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author lvchuan
 * @description 异步方法代理
 * @date 2023/5/23 10:47:50
 */
@Slf4j
public class AsyncProxyUtil implements MethodInterceptor {
    public static Map<String, Map<String, AsyncDTO>> asyncMethod = new HashMap<>();

    @Resource
    private AsyncHandler asyncHandler;

    public <T> T proxy(T t) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Map<String, AsyncDTO> proxyMethodList = AsyncProxyUtil.asyncMethod.get(method.getDeclaringClass().getName());
        if (Objects.isNull(proxyMethodList) || CollectionUtils.isEmpty(proxyMethodList)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if (Objects.isNull(proxyMethodList.get(method.getName()))) {
            return methodProxy.invokeSuper(o, objects);
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
            asyncParamDTO.setType(parameters[i].getType().getName());
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
        asyncHandler.sendMsg(asyncMsgDTO);
        return null;
    }
}
