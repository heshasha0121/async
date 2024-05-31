package com.lvchuan.imports;

import java.util.List;

/**
 * @description: 导入处理实现类
 * @author: lvchuan
 * @createTime: 2024-05-29 14:27
 */
public interface IImportHandler<T> {
    ImportTemplateBizType type();

    /**
     * 是否校验参数
     * @return
     */
    default boolean isCheckParam() {
        return true;
    }

    /**
     * 构建配置
     * @return
     */
    default ImportTemplateConfigDTO buildConfig() {
        return ImportTemplateConfigDTO.builder().build();
    }

    /**
     * 处理数据
     * @param dataList
     */
    void handleData(List<T> dataList);
}
