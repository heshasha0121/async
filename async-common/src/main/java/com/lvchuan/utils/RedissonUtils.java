package com.lvchuan.utils;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author lty
 */
@Slf4j
@Component
public class RedissonUtils {

    @Resource
    private RedissonClient redissonClient;

    public static final Long DEFAULT_WAIT_MILL_SECOND_TIME = 200L;

    public static final Long DEFAULT_EXPIRE_MILL_SECOND_TIME = 20 * 1000L;

    /**
     * @return redissonClient 链接
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public RMap<Object, Object> getMap(String mapName) {
        return redissonClient.getMap(mapName);
    }


    /**
     * @param lockKey 锁名称
     * @return 加锁结果
     */
    public Boolean lock(String lockKey) {
        return this.lock(DEFAULT_WAIT_MILL_SECOND_TIME, DEFAULT_EXPIRE_MILL_SECOND_TIME, lockKey);
    }


    /**
     * @param expireTime 过期时间
     * @param key        锁名称
     * @return 加锁结果
     */
    public Boolean lock(Long expireTime, String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            return lock.tryLock(0L, expireTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("redis加锁失败,lockKey:{},异常:{}", key, e);
            return Boolean.FALSE;
        }
    }

    /**
     * @param waitTime   等待时间
     * @param expireTime 过期时间
     * @param key        锁名称
     * @return 加锁结果
     */
    public Boolean lock(Long waitTime, Long expireTime, String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            return lock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("加锁失败,lockKey:{},异常:", key, e);
            return Boolean.FALSE;
        }
    }


    /**
     * 释放锁
     *
     * @param key 锁key
     */
    public void unlock(String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            lock.unlock();
        } catch (Exception e) {
            log.warn("redis解锁失败,lockKey:{},thread-id:{},异常:", key, Thread.currentThread().getId(), e);
        }

    }

    /**
     * @param key      bucketKey
     * @param oldValue 旧值
     * @param newValue 新值
     * @return 结果
     */
    public Boolean compareAndSetBucket(String key, Object oldValue, Object newValue) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.compareAndSet(oldValue, newValue);
    }

    /**
     * @param key bucketKey
     * @return 值
     */
    public <T> T getBucketValue(String key, Class<T> clazz) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }


}
