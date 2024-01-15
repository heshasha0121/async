package com.lvchuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @description: ${description}
 * @author: lvchuan
 * @createTime: 2024-01-03 13:44
 */
@SpringBootApplication()
@EnableCaching
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}