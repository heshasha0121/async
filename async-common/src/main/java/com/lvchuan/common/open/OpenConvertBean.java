package com.lvchuan.common.open;

import java.lang.annotation.*;

/**
 * @description: 对外转换
 * @author: lvchuan
 * @createTime: 2024-11-07 11:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface OpenConvertBean {
}
