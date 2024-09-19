package com.lvchuan.service;

import com.lvchuan.imports.IImportHandler;
import com.lvchuan.imports.ImportTemplateBizType;
import com.lvchuan.imports.ImportTemplateConfigDTO;
import com.lvchuan.model.TestImportModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 测试导入
 * @author: lvchuan
 * @createTime: 2024-05-30 15:02
 */
@Slf4j
@Component
public class TestImportService implements IImportHandler<TestImportModel> {
    @Override
    public ImportTemplateBizType type() {
        return ImportTemplateBizType.TEST;
    }

    @Override
    public void handleData(List<TestImportModel> dataList) {
        log.info("业务处理数据");
    }
}
