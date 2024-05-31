package com.lvchuan.common.enums;

import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 枚举转化器
 * @author: lvchuan
 * @createTime: 2024-05-29 11:31
 */
public class IntegerToEnumConverter<T extends BaseEnum> implements Converter<String, BaseEnum> {
    private Map<String, BaseEnum> enumMap = new HashMap<>();

    public IntegerToEnumConverter(Class<BaseEnum> enumType) {
        //把所有枚举对象读取出来
        BaseEnum[] enums = enumType.getEnumConstants();
        for (BaseEnum e : enums) {
            //枚举对象的code作为key，枚举对象作为value
            enumMap.put(e.getCode().toString(), e);
        }
    }

    @Override
    public BaseEnum convert(String integer) {
        return enumMap.get(integer);
    }
}
