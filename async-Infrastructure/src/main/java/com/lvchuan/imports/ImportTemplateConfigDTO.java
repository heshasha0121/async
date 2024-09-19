package com.lvchuan.imports;

import lombok.Builder;
import lombok.Data;

/**
 * @description: 导入配置类
 * @author: lvchuan
 * @createTime: 2024-05-29 14:21
 */
@Data
@Builder
public class ImportTemplateConfigDTO {

    /**
     * 头行数 默认一行
     */
    @Builder.Default
    private int headRowNum = 1;
}
