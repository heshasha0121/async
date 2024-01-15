package com.lvchuan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 异步导出日志状态
 * @author: lvchuan
 * @createTime: 2023-12-11 14:08
 */
@AllArgsConstructor
@Getter
public enum AsyncExportTaskStatusEnum implements BaseEnum {
    /**
     * 等待导出
     */
    WAIT(0, "等待导出"),
    /**
     * 导出中
     */
    ING(10, "导出中"),
    /**
     * 导出完成
     */
    FINISH(20, "导出完成"),
    /**
     * 导出异常
     */
    ERROR(30, "导出异常");

    private Integer code;
    private String desc;
}
