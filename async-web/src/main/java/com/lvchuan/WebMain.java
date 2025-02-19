package com.lvchuan;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQTransactionConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

/**
 * @description: ${description}
 * @author: lvchuan
 * @createTime: 2024-01-03 13:44
 */
@SpringBootApplication(scanBasePackages = "com.lvchuan",
        exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class, RedisAutoConfiguration.class})
@EnableCaching
@Import(RocketMQAutoConfiguration.class)
public class WebMain {
    public static void main(String[] args) {
        SpringApplication.run(WebMain.class, args);
    }
}