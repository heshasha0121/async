package com.lvchuan.controller;

import com.lvchuan.export.DynamicExportFactory;
import com.lvchuan.model.ExportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @description: 导出服务api
 * @author: lvchuan
 * @createTime: 2023-12-10 9:59
 */
@RestController
@RequestMapping("/export")
public class ExportController {
    @Autowired
    private DynamicExportFactory dynamicExportFactory;

    @PostMapping("/syncExport")
    public String syncExport(@RequestBody @Validated ExportReq exportReq) {
        return dynamicExportFactory.syncExport(exportReq.getParam(), exportReq.getBizType());
    }

    @PostMapping("/asyncExport")
    public Long asyncExport(@RequestBody @Validated ExportReq exportReq) {
        return dynamicExportFactory
                .asyncExport(exportReq.getParam(), exportReq.getBizType());
    }
}
