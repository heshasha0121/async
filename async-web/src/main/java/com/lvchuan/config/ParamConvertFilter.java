package com.lvchuan.config;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lvchuan.common.open.OpenConvertBean;
import com.lvchuan.common.open.OpenConvertId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description: 入参转换拦截
 * @author: lvchuan
 * @createTime: 2024-11-07 10:55
 */
@Slf4j
@RestControllerAdvice
@Component
public class ParamConvertFilter implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return AnnotationUtil.hasAnnotation((Class) type, OpenConvertBean.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        InputStream inputStream = httpInputMessage.getBody();
        String body = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(body)) {
            return httpInputMessage;
        }
        JSONObject jsonObject = JSON.parseObject(body);
        Class paramClass = (Class) type;
        this.convert(paramClass, jsonObject);
        InputStream rawInputStream = new ByteArrayInputStream(jsonObject.toJSONString().getBytes("UTF-8"));
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return rawInputStream;
            }
        };
    }

    /**
     * 转换id
     * @param clazz
     * @param jsonObject
     */
    private void convert(Class clazz, JSONObject jsonObject) {
        Field[] fieldArr = ClassUtil.getDeclaredFields(clazz);
        for (Field field : fieldArr) {
            Object openValue = jsonObject.get(field.getName());
            if (field.isAnnotationPresent(OpenConvertId.class)) {
                OpenConvertId openConvert = field.getAnnotation(OpenConvertId.class);
                //todo 保存映射关系并获取映射后的id
                Long id = IdWorker.getId();
                jsonObject.put(field.getName(), id);
            } else if (field.isAnnotationPresent(OpenConvertBean.class)) {
                if (field.getType() == List.class) {
                    Type genericType = field.getGenericType();
                    ParameterizedType pt = (ParameterizedType) genericType;
                    //得到泛型里的class类型对象
                    Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                    for (int i = 0; i < ((JSONArray) openValue).size(); i++) {
                        this.convert(genericClazz, ((JSONArray) openValue).getJSONObject(i));
                    }
                } else {
                    this.convert(field.getType(), (JSONObject) openValue);
                }
            }
        }
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
