package com.lvchuan.common.exception;

import com.lvchuan.common.web.ResultEnum;
import lombok.Data;

/**
 * @description: 也未统一报错
 * @author: lvchuan
 * @createTime: 2024-05-30 17:10
 */
@Data
public class BusinessException extends RuntimeException {
    private int code;

    private String message;

    public BusinessException(ResultEnum resultEnum) {
        super(resultEnum.getDesc());
        this.code = resultEnum.getCode();
        this.message = resultEnum.getDesc();
    }

    public BusinessException(ResultEnum resultEnum, String message) {
        super(message);
        this.code = resultEnum.getCode();
        this.message = message;
    }
}
