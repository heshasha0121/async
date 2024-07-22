package com.lvchuan.config.mongo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.lvchuan.common.enums.BaseEnum;
import com.lvchuan.common.mongo.MongoEnumData;
import com.lvchuan.common.mongo.MongoEnumReadConverter;
import com.lvchuan.common.mongo.MongoEnumWriteConverter;
import com.lvchuan.config.EnumConfig;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author lvchuan
 * @description mongo配置
 * @date 2023/11/1 17:08:18
 */
@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        Set<Class<?>> baseEnumSet = ClassUtil.scanPackageBySuper(EnumConfig.SCAN_PACKAGE, BaseEnum.class);
        List<Class> mongoEnumClassList = new ArrayList<>();
        for (Class clazz : baseEnumSet) {
            if (BooleanUtils.isFalse(clazz.isEnum())) {
                continue;
            }
            Annotation mongoEnumData = clazz.getAnnotation(MongoEnumData.class);
            if (Objects.nonNull(mongoEnumData)) {
                mongoEnumClassList.add(clazz);
            }
        }
        MongoCustomConversions mongoCustomConversions = MongoCustomConversions.create(mongoConverterConfigurationAdapter -> {
            if (CollUtil.isNotEmpty(mongoEnumClassList)) {
                mongoConverterConfigurationAdapter.registerConverters(Collections.singleton(new MongoEnumReadConverter(mongoEnumClassList)));
                mongoConverterConfigurationAdapter.registerConverters(Collections.singleton(new MongoEnumWriteConverter(mongoEnumClassList)));
            }
        });
        return mongoCustomConversions;
    }
}
