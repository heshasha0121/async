package com.lvchuan.common.lock;

import java.lang.annotation.*;

/**
 * @author lvchuan
 * @description 简易redis加锁方法
 * @date 2023/10/27 09:50:19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SimpleRedisLockMethod {
    /**
     * key 支持spel
     *
     * @return key
     */
    String key();

    /**
     * 超时时间（毫秒值），默认20s
     *
     * @return 超时时间
     */
    long expireTime() default 0;

    /**
     * 等待时间
     *
     * @return 等大时间，默认200ms
     */
    long waitTime() default 0;

    /**
     * 获取锁失败报错提示，默认报错：获取锁失败
     *
     * @return 获取锁失败提示原因
     */
    String obtainLockErrMsg() default "";

    /**
     * redis key是否自动添加租户条件，默认值为true
     * true key自动带上租户条件 {tenantCode}:{key}，如果没有查询到租户code，将不会加锁
     * false key不带租户条件
     * @return 是否带上租户条件
     */
    boolean autoTenantOfKey() default true;

    /**
     * 锁key前缀
     * @return
     */
    String keyPrefix() default "";
}
