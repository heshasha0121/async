package com.lvchuan.service.ai;

import com.lvchuan.model.AIChatReq;

/**
 * @description: ai接口
 * @author: lvchuan
 * @createTime: 2025-01-21 10:09
 */
public interface IAIService {
    String chasMsg(AIChatReq msg);
}
