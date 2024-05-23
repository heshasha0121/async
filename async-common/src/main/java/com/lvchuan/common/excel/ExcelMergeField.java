package com.lvchuan.common.excel;

import java.lang.annotation.*;

/**
 * @description: 列表合并字段
 * @author: lvchuan
 * @createTime: 2024-05-23 10:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelMergeField {
}
