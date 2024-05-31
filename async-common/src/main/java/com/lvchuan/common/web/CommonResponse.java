package com.lvchuan.common.web;

import lombok.Data;

/**
 * @description: 公共反参
 * @author: lvchuan
 * @createTime: 2024-05-30 17:26
 */
@Data
public class CommonResponse <T> {
    private Integer code;
    private T data;
    private Boolean success;

    public CommonResponse(Integer code , T data, Boolean success) {
        this.code = code;
        this.data = data;
        this.success = success;
    }

    public static CommonResponse returnSuccess(Object data) {
        return new CommonResponse(200, data, true);
    }

    public static CommonResponse returnError(Object data) {
        return new CommonResponse(400, data, false);
    }

    public static CommonResponse returnError(int code ,Object data) {
        return new CommonResponse(code, data, false);
    }
}
