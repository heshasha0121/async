package com.lvchuan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 对外id转换类型枚举
 * @author: lvchuan
 * @createTime: 2024-11-07 11:26
 */
@AllArgsConstructor
@Getter
public enum OpenConvertIdTypeEnum implements BaseEnum {
    /**
     * 测试ID
     */
    TEST(1, "测试ID"),
    /**
     * 测试明细ID
     */
    TEST_DETAIL(2, "测试明细ID"),
    ;

    @EnumValue
    private Integer code;
    private String desc;
}
