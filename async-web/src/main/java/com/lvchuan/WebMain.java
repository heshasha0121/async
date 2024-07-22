package com.lvchuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @description: ${description}
 * @author: lvchuan
 * @createTime: 2024-01-03 13:44
 */
@SpringBootApplication(scanBasePackages = "com.lvchuan",
        exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableCaching
public class WebMain {
    public static void main(String[] args) {
        SpringApplication.run(WebMain.class, args);
    }
}