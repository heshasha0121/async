package com.lvchuan.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 导出模板字段配置表
 * </p>
 *
 * @author peace
 * @since 2023-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportTemplateConfig implements Serializable {

    private String fieldName;
    private String showName;
}
