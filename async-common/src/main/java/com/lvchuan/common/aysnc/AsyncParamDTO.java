package com.lvchuan.common.aysnc;

import lombok.Data;

/**
 * @author lvchuan
 * @description 异步参数类型
 * @date 2023/5/24 09:39:40
 */
@Data
public class AsyncParamDTO {

    /**
     * 方法类型
     */
    private String type;

    /**
     * 源类型
     */
    private String sourceType;

    private Object value;
}
