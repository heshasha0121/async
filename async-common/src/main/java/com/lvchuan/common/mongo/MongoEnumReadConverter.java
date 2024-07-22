package com.lvchuan.common.mongo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import com.lvchuan.common.enums.BaseEnum;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author lvchuan
 *
 * @description mongo enum read
 * @date 2023/11/2 13:50:24
 */
@ReadingConverter
public class MongoEnumReadConverter implements GenericConverter {
    private List<Class> mongoEnumClassList;

    public MongoEnumReadConverter(List<Class> mongoEnumClassList) {
        this.mongoEnumClassList = mongoEnumClassList;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> enumSet = new HashSet<>();
        for (Class temp : mongoEnumClassList){
            ConvertiblePair convertiblePair = new ConvertiblePair(Integer.class, temp);
            enumSet.add(convertiblePair);
        }
        return enumSet;
    }

    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class targetClazz = targetType.getType();
        Map<String, BaseEnum> baseEnumMap = EnumUtil.getEnumMap(targetClazz);
        if (CollUtil.isEmpty(baseEnumMap)) {
            return null;
        }
        for (BaseEnum entry : baseEnumMap.values()) {
            if (Objects.equals(entry.getCode(), source)) {
                return entry;
            }
        }
        return null;
    }
}
