package com.lvchuan.common.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 枚举转换
 * @author: lvchuan
 * @createTime: 2024-05-29 11:27
 */
public class IntegerToEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    private static final Map<Class, Converter> CONVERTERS = new HashMap<>();

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> aClass) {
        IntegerToEnumConverter converter = (IntegerToEnumConverter) CONVERTERS.get(aClass);
        if (converter == null) {
            converter = new IntegerToEnumConverter(aClass);
            CONVERTERS.put(aClass, converter);
        }
        return converter;
    }
}
