package com.lvchuan.controller;

import com.lvchuan.common.web.CommonResponse;
import com.lvchuan.model.TestImportModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 转换测试
 * @author: lvchuan
 * @createTime: 2024-11-07 17:59
 */
@RestController
@RequestMapping("/mq")
public class OpenController {
    @PostMapping("/test")
    public CommonResponse test(@RequestBody @Validated TestImportModel param) {
        return CommonResponse.returnSuccess("ss");
    }
}
