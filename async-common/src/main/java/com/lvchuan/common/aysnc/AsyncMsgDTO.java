package com.lvchuan.common.aysnc;

import lombok.Data;

import java.util.List;

/**
 * @author lvchuan
 * @description 异步消息对象
 * @date 2023/5/23 15:59:01
 */
@Data
public class AsyncMsgDTO {

    private Long id;

    private String beanName;

    private String value;

    private String bizCode;

    private List<AsyncParamDTO> paramList;

    private String methodName;
}
