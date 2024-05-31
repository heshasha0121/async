package com.lvchuan.common.web;

import com.lvchuan.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 错误代码
 * @author: lvchuan
 * @createTime: 2024-05-30 17:11
 */
@AllArgsConstructor
@Getter
public enum ResultEnum implements BaseEnum {
    SYS_ERROR(500, "系统错误");

    private Integer code;
    private String desc;
}
