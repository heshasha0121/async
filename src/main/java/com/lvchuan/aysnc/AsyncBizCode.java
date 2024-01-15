package com.lvchuan.aysnc;

import java.lang.annotation.*;

/**
 * @author lvchuan
 * @description 异步业务编号注解
 * @date 2023/5/23 15:33:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface AsyncBizCode {
}
