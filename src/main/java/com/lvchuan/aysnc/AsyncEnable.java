package com.lvchuan.aysnc;

import java.lang.annotation.*;

/**
 * @author lvchuan
 * @description 异步执行方法注解
 * @date 2023/5/23 09:19:19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AsyncEnable {
    String value() default "";
}
