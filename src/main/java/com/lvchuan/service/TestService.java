package com.lvchuan.service;

import com.lvchuan.common.aysnc.AsyncBizCode;
import com.lvchuan.common.aysnc.AsyncEnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description: 测试service
 * @author: lvchuan
 * @createTime: 2024-03-06 15:51
 */
@Service
@Slf4j
@AsyncEnable
public class TestService {

    @AsyncEnable("测试异步执行方法")
    public void async(@AsyncBizCode Long id) {
        log.info("id:{}", id);
    }
}
