package com.lvchuan.controller;

import com.lvchuan.common.web.CommonResponse;
import com.lvchuan.export.ExportTemplateBizTypeEnum;
import com.lvchuan.imports.ImportFactory;
import com.lvchuan.imports.ImportTemplateBizType;
import com.lvchuan.model.TestImportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 导入公共api
 * @author: lvchuan
 * @createTime: 2024-05-29 9:49
 */
@RestController
@RequestMapping("/import")
public class ImportController {
    @Autowired
    private ImportFactory importFactory;

    @GetMapping("/common")
    public void asyncExport(@RequestParam("file") MultipartFile file,
                              @RequestParam("type") ImportTemplateBizType type) {
        importFactory.doImport(file, type);
    }

    @PostMapping("/test")
    public CommonResponse test(@RequestBody @Validated TestImportModel param) {
        return CommonResponse.returnSuccess("ss");
    }
}
