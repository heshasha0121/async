package com.lvchuan.export;

import com.lvchuan.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 溯源导出模板
 * @author: lvchuan
 * @createTime: 2023-12-06 11:17
 */
@AllArgsConstructor
@Getter
public enum ExportTemplateBizTypeEnum implements BaseEnum {
    /**
     * 溯源导出
     */
    TEST(1, "测试导出"),
    ;
    private Integer code;
    private String desc;
}
