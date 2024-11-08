package com.lvchuan.common.open;


import com.lvchuan.common.enums.OpenConvertIdTypeEnum;

import java.lang.annotation.*;

/**
 * @description: 对外Id转换注解
 * @author: lvchuan
 * @createTime: 2024-11-07 11:24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OpenConvertId {
    OpenConvertIdTypeEnum type();
}
