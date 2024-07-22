package com.lvchuan.common.enums;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lvchuan
 * @description 基础枚举VO
 * @date 2023/10/30 17:55:34
 */
@Data
public class BaseEnumVO {
    @ApiModelProperty("code")
    private Integer code;

    @ApiModelProperty("value")
    private String value;
}
