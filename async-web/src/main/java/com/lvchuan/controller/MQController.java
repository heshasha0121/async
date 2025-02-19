package com.lvchuan.controller;

import com.alibaba.fastjson.JSON;
import com.lvchuan.common.aysnc.AsyncProxyUtil;
import com.lvchuan.common.web.CommonResponse;
import com.lvchuan.imports.ImportTemplateBizType;
import com.lvchuan.model.TestImportModel;
import com.lvchuan.mq.MQProducerService;
import com.lvchuan.service.TestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @description: mq
 * @author: lvchuan
 * @createTime: 2024-06-12 14:52
 */
@RestController
@RequestMapping("/mq")
public class MQController {
    @Autowired
    private MQProducerService mqProducerService;
    @Autowired
    private AsyncProxyUtil asyncProxyUtil;
    @PostMapping("/send")
    public void test(@RequestBody Object msg) {
        mqProducerService.sendCommon(JSON.toJSONString(msg));
    }

    @Autowired
    private TestService testService;

    @PostMapping("/sendTest")
    public void testAsync() {
        asyncProxyUtil.proxy(testService).async(122L);
    }
}
