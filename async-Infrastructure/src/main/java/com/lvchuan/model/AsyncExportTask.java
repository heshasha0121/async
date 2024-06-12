package com.lvchuan.model;

import com.lvchuan.enums.AsyncExportTaskStatusEnum;
import com.lvchuan.export.ExportTemplateBizTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 异步导出日志表
 * </p>
 *
 * @author lvchuan
 * @since 2023-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AsyncExportTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private AsyncExportTaskStatusEnum status;

    private String taskName;

    private String fileUrl;

    private String fileName;

    private Long userId;

    private String tenantCode;

    private Date gmtCreate;

    private Date gmtModified;

    private ExportTemplateBizTypeEnum bizType;

    private Boolean isDelete;
}
