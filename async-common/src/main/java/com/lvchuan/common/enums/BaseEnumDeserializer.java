package com.lvchuan.common.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

/**
 * @author lvchuan
 * @description sss
 * @date 2023/8/3 11:37:34
 */
@Slf4j
public class BaseEnumDeserializer extends JsonDeserializer<BaseEnum> {
    @Override
    public BaseEnum deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode value = jsonParser.getCodec().readTree(jsonParser);
        String currentName = jsonParser.currentName();
        Object currentValue = jsonParser.getCurrentValue();
        Class findPropertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
        if (findPropertyType.isEnum() && BaseEnum.class.isAssignableFrom(findPropertyType)) {
            BaseEnum[] enumConstants = (BaseEnum[]) findPropertyType.getEnumConstants();
            for (BaseEnum e : enumConstants) {
                if (e.getCode().equals(value.asInt())) {
                    return e;
                }
            }
        }
        return null;
    }
}
