package com.lvchuan.common.mongo;

import com.lvchuan.common.enums.BaseEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lvchuan
 *
 * @description mongo enum write
 * @date 2023/11/2 13:50:24
 */
@WritingConverter
public class MongoEnumWriteConverter implements GenericConverter {
    private List<Class> mongoEnumClassList;

    public MongoEnumWriteConverter(List<Class> mongoEnumClassList) {
        this.mongoEnumClassList = mongoEnumClassList;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> enumSet = new HashSet<>();
        for (Class temp : mongoEnumClassList){
            ConvertiblePair convertiblePair = new ConvertiblePair(temp, Integer.class);
            enumSet.add(convertiblePair);
        }
        return enumSet;
    }

    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class sourceClass = sourceType.getType();
        if (BooleanUtils.isFalse(sourceClass.isEnum())) {
            return null;
        }
        if (BooleanUtils.isFalse(BaseEnum.class.isAssignableFrom(sourceClass))) {
            return null;
        }
        BaseEnum baseEnum = (BaseEnum) source;
        return baseEnum.getCode();
    }
}
