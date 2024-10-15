package com.lvchuan.common.log;

import java.lang.annotation.*;

/**
 * @description: 日志显示字段
 * @author: lvchuan
 * @createTime: 2024-09-25 11:20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface LogShowField {
}
