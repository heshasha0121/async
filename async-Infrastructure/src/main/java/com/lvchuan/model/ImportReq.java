package com.lvchuan.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lvchuan.common.enums.BaseEnumDeserializer;
import com.lvchuan.export.ExportTemplateBizTypeEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @description: 导入
 * @author: lvchuan
 * @createTime: 2024-05-29 10:59
 */
@Data
public class ImportReq {
    @NotNull(message = "类型不能为空")
    @JsonDeserialize(using = BaseEnumDeserializer.class)
    private ExportTemplateBizTypeEnum type;
}
