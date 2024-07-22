package com.lvchuan.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.EnumUtil;
import com.lvchuan.common.enums.BaseEnum;
import com.lvchuan.common.enums.BaseEnumVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author lvchuan
 * @description 枚举配置
 * @date 2023/10/30 17:53:11
 */
@Configuration
@Slf4j
public class EnumConfig {
    public static final String SCAN_PACKAGE = "com.ydsz.erp.srm.enums";

    @Bean
    public Map<String, List<BaseEnumVO>> baseEnumMap() {
        Set<Class<?>> baseEnumSet = ClassUtil.scanPackageBySuper(SCAN_PACKAGE, BaseEnum.class);
        if (CollUtil.isEmpty(baseEnumSet)) {
            return null;
        }
        Map<String, List<BaseEnumVO>> result = new HashMap<>(8);
        for (Class clazz : baseEnumSet) {
            if (BooleanUtils.isFalse(clazz.isEnum())) {
                continue;
            }
            String baseName = clazz.getName();
            String enumKey = baseName.replace(SCAN_PACKAGE, "").replace(".", "");
            List<BaseEnumVO> baseEnumVOList = new ArrayList<>();
            Map<String, BaseEnum> baseEnumMap = EnumUtil.getEnumMap(clazz);
            if (CollUtil.isEmpty(baseEnumMap)) {
                continue;
            }
            for (Map.Entry<String, BaseEnum> entry : baseEnumMap.entrySet()) {
                BaseEnum value = entry.getValue();
                BaseEnumVO baseEnumVO = new BaseEnumVO();
                baseEnumVO.setCode(value.getCode());
                baseEnumVO.setValue(value.getDesc());
                baseEnumVOList.add(baseEnumVO);
            }
            result.put(enumKey, baseEnumVOList);
        }
        return result;
    }
}
