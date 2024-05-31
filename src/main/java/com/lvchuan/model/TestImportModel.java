package com.lvchuan.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @description: 测试导入参数
 * @author: lvchuan
 * @createTime: 2024-05-30 15:38
 */
@Data
public class TestImportModel {
    @NotBlank(message = "名称不能为空")
    @ExcelProperty("名称")
    private String name;

    private String phone;
}
