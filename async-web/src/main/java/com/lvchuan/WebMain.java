package com.lvchuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @description: ${description}
 * @author: lvchuan
 * @createTime: 2024-01-03 13:44
 */
@SpringBootApplication(scanBasePackages = "com.lvchuan")
@EnableCaching
public class WebMain {
    public static void main(String[] args) {
        SpringApplication.run(WebMain.class, args);
    }
}