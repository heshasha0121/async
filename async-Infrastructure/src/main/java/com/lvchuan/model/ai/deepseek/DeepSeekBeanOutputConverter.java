package com.lvchuan.model.ai.deepseek;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;


/**
 * @description: DeepSeek转换器
 * @author: lvchuan
 * @createTime: 2025-02-20 13:44
 */
@Slf4j
public class DeepSeekBeanOutputConverter<T> extends BeanOutputConverter<T> {

    public DeepSeekBeanOutputConverter(Class<T> clazz) {
        super(clazz);
    }

    public DeepSeekBeanOutputConverter(Class<T> clazz, ObjectMapper objectMapper) {
        super(clazz, objectMapper);
    }

    public DeepSeekBeanOutputConverter(ParameterizedTypeReference<T> typeRef) {
        super(typeRef);
    }

    public DeepSeekBeanOutputConverter(ParameterizedTypeReference<T> typeRef, ObjectMapper objectMapper) {
        super(typeRef, objectMapper);
    }

    @Override
    public T convert(@NonNull String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        log.info("deepseek response: {}", text);
        text = text.substring(text.indexOf("```json"));
        return (T) super.convert(text);
    }
}
