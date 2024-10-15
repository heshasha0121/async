package com.lvchuan.common.log;

import java.lang.annotation.*;

/**
 * @description: 日志描述
 * @author: lvchuan
 * @createTime: 2024-07-22 10:44
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface LogDesc {
    Class<? extends ILogDescHandler> value();

    /**
     * 入参字段名称
     */
    String paramFiledName() default "";
}
