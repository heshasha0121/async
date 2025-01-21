package com.lvchuan.model.ai.coze;

import lombok.Data;

import java.util.List;

/**
 * @description: body
 * @author: lvchuan
 * @createTime: 2025-01-21 10:21
 */
@Data
public class Body {
    private String bot_id;
    private String user_id;
    private List<AdditionalMessages> additional_messages;
}
