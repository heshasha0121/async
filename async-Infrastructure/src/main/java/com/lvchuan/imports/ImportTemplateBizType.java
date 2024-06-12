package com.lvchuan.imports;

import com.lvchuan.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 导入模板枚举
 * @author: lvchuan
 * @createTime: 2024-05-29 10:18
 */
@AllArgsConstructor
@Getter
public enum ImportTemplateBizType implements BaseEnum {
    /**
     * 测试导入
     */
    TEST(1, "测试导入"),
    ;

    private Integer code;
    private String desc;
}
