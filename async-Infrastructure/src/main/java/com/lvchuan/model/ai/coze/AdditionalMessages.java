package com.lvchuan.model.ai.coze;

import lombok.Data;

/**
 * @description: additional_messages
 * @author: lvchuan
 * @createTime: 2025-01-21 10:25
 */
@Data
public class AdditionalMessages {
    private String role = "user";

    //private String type = "answer";

    private String content;
}
