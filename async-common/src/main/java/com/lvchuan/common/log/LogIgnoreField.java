package com.lvchuan.common.log;

import java.lang.annotation.*;

/**
 * @description: 日志忽略字段
 * @author: lvchuan
 * @createTime: 2024-10-11 16:39
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface LogIgnoreField {
}
