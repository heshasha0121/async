package com.lvchuan.aysnc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author lvchuan
 * @description 异步方法代理
 * @date 2023/5/23 10:47:50
 */
@Slf4j
@Component
public class AsyncProxyUtil implements MethodInterceptor {
    public static Map<String, Map<String, AsyncDTO>> asyncMethod = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${asyncMethod.isAsync:true}")
    private Boolean isAsync;


    @Value("${spring.application.name:async}")
    private String applicationName;

    public final String common_async_method_tag = "common_async_method_tag";

    public String getTagName() {
        String tag = applicationName + "_" + common_async_method_tag;
        return tag.replace("-", "_");
    }

    public <T> T proxy(T t) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //控制是否走异步处理总开关
        if (isAsync) {
            return methodProxy.invokeSuper(o, objects);
        }
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

        // todo 消息发送
        Boolean send = true;
        if (!send) {
            throw new RuntimeException("异步消息发送失败");
        }
        return null;
    }

    public void doAsyncMsg(AsyncMsgDTO dto, boolean isDebug) {
        log.info("开始执行消息");
        try {
            if (StringUtils.isEmpty(dto.getBeanName())) {
                throw new RuntimeException("未设置bean名称");
            }
            if (StringUtils.isEmpty(dto.getMethodName())) {
                throw new RuntimeException("未设置需要执行的方法");
            }
            Object object = applicationContext.getBean(dto.getBeanName());
            if (Objects.isNull(object)) {
                throw new RuntimeException("未获取到对象");
            }
            Class[] classArr = new Class[dto.getParamList().size()];
            if (!CollectionUtils.isEmpty(dto.getParamList())) {
                for (int i = 0; i < dto.getParamList().size(); i++) {
                    Class paramClass = Class.forName(dto.getParamList().get(i).getType());
                    classArr[i] = paramClass;
                }
            }
            Object[] param = new Object[dto.getParamList().size()];
            Method method = ReflectionUtils.findMethod(object.getClass(), dto.getMethodName(), classArr);
            if (Objects.isNull(method)) {
                throw new RuntimeException("未获取到需要执行的方法");
            }
            if (!CollectionUtils.isEmpty(dto.getParamList())) {
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Object value = dto.getParamList().get(i).getValue();
                    Class<?> paramClass = parameters[i].getType();
                    if (Objects.isNull(value)) {
                        param[i] = null;
                    } else if (paramClass == Long.class) {
                        param[i] = Long.parseLong(String.valueOf(value));
                    } else if (paramClass == BigDecimal.class) {
                        param[i] = new BigDecimal(String.valueOf(value));
                    } else if (paramClass == Integer.class) {
                        param[i] = value;
                    } else if (paramClass.isEnum()) {
                        Object[] enumConstants = parameters[i].getType().getEnumConstants();
                        Optional<Object> select = Arrays.stream(enumConstants)
                                .filter(item -> item.toString().equalsIgnoreCase(String.valueOf(value)))
                                .findFirst();
                        if (select.isPresent()) {
                            param[i] = select.get();
                        }
                    } else if (paramClass == String.class) {
                        param[i] = value;
                    } else if (paramClass == List.class) {
                        Type genericType = parameters[i].getParameterizedType();
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                        param[i] = JSON.parseArray(((JSONArray)value).toJSONString(), genericClazz);
                    } else if (paramClass == Set.class) {
                        Type genericType = parameters[i].getParameterizedType();
                        param[i] = JSON.parseObject(((JSONArray)value).toJSONString(), genericType);
                    } else if (paramClass == Map.class) {
                        Type genericType = parameters[i].getParameterizedType();
                        param[i] = JSON.parseObject(((JSONObject)value).toJSONString(), genericType);
                    } else if (paramClass == Date.class) {
                        param[i] = new Date( Long.parseLong(String.valueOf(value)));
                    } else {
                        param[i] = JSONObject.toJavaObject((JSONObject) value, paramClass);
                    }
                }
            }
            method.invoke(object, param);
        } catch (InvocationTargetException ex) {
            log.error("执行异步消息方法失败===={}===={}", dto.getBizCode(), ex.getTargetException());
            //方法内部错误
            throw new RuntimeException(ex.getTargetException().getMessage());
        } catch (Exception e) {
            log.error("执行异步消息方法失败===={}===={}", dto.getBizCode(), e);
            throw new RuntimeException("执行异步消息方法失败");
        }
        log.info("执行成功");
    }
}
