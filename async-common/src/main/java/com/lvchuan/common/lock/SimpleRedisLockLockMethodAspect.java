package com.lvchuan.common.lock;

import cn.hutool.core.util.StrUtil;
import com.lvchuan.common.exception.BusinessException;
import com.lvchuan.common.web.ResultEnum;
import com.lvchuan.utils.RedissonUtils;
import com.lvchuan.utils.SpelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author lvchuan
 *
 * @description 简易redis锁加锁aop
 * @date 2023/10/27 09:58:58
 */
@Aspect
@Component
@Slf4j
public class SimpleRedisLockLockMethodAspect {
    @Autowired
    private RedissonUtils redissonUtils;

    private static final String DEF_LOCK_ERROR_MSG =  "获取锁失败";


    @Around("@annotation(com.lvchuan.common.lock.SimpleRedisLockMethod)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature();
        Method method = joinPointObject.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] objArr = joinPoint.getArgs();
        if (Objects.isNull(parameters) || parameters.length == 0) {
            return this.proceed(joinPoint);
        }
        SimpleRedisLockMethod lockInfo = method.getAnnotation(SimpleRedisLockMethod.class);
        String key = SpelUtils.parse(method, objArr, lockInfo.key(), String.class, "") ;
        if (StrUtil.isBlank(key)) {
            log.warn("简易锁key为空：{}", method.getName());
            return this.proceed(joinPoint);
        }
        String keyPrefix = lockInfo.keyPrefix();
        if (StrUtil.isNotBlank(keyPrefix)) {
            key = keyPrefix + ":" + key;
        }
        Long expireTime = lockInfo.expireTime();
        Long waitTime = lockInfo.waitTime();
        String lockErrMsg = DEF_LOCK_ERROR_MSG;
        if (StrUtil.isNotBlank(lockInfo.obtainLockErrMsg())) {
            lockErrMsg = lockInfo.obtainLockErrMsg();
        }
        if (expireTime == 0) {
            expireTime = RedissonUtils.DEFAULT_EXPIRE_MILL_SECOND_TIME;
        }
        if (waitTime == 0) {
            waitTime = RedissonUtils.DEFAULT_WAIT_MILL_SECOND_TIME;
        }
        boolean lock = redissonUtils.lock(waitTime, expireTime, key);
        if (BooleanUtils.isFalse(lock)) {
            throw new BusinessException(ResultEnum.SYS_ERROR, lockErrMsg);
        }
        try {
            return this.proceed(joinPoint);
        } finally {
            redissonUtils.unlock(key);
        }
    }

    /**
     * 执行原方法
     *
     * @param joinPoint 切点
     * @return 返回值
     */
    private Object proceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (BusinessException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
