package com.lvchuan.common.aysnc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description: 异步处理实现接口
 * @author: lvchuan
 * @createTime: 2024-03-06 16:02
 */
@Slf4j
public abstract class AsyncHandler {
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送消息
     * @param asyncMsgDTO 解析后的参数消息
     * @return 是否发送成功
     */
    public abstract void sendMsg(AsyncMsgDTO asyncMsgDTO);

    /**
     * 消息返回后处理数据
     * @param dto 返回的消息体
     */
    public final void handleMsg(AsyncMsgDTO dto) {
        if (Objects.isNull(dto)) {
            log.warn("没有需要处理的消息");
            return;
        }
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
