package com.lvchuan.controller;

import com.lvchuan.model.AIChatReq;
import com.lvchuan.service.ai.IAIService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: ai api接口
 * @author: lvchuan
 * @createTime: 2025-01-21 10:10
 */
@RestController
@RequestMapping("/ai")
public class AIController {
    @Resource
    private IAIService aiService;

    @PostMapping("/chasMsg")
    public String chasMsg(@RequestBody @Validated AIChatReq data) {
        return aiService.chasMsg(data);
    }
}
