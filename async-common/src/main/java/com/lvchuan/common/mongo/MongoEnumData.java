package com.lvchuan.common.mongo;

import java.lang.annotation.*;

/**
 * @author lvchuan
 * @description mongo枚举数据转换
 * @date 2023/11/2 11:13:56
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MongoEnumData {
}
