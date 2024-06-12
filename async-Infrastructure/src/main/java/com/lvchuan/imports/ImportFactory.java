package com.lvchuan.imports;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.lvchuan.common.exception.BusinessException;
import com.lvchuan.common.web.ResultEnum;
import com.lvchuan.utils.ValidateUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 导入工厂类
 * @author: lvchuan
 * @createTime: 2024-05-29 14:28
 */
@Component
@Log4j2
public class ImportFactory implements InitializingBean {
    @Autowired
    private List<IImportHandler> importHandlerList;
    private static Map<ImportTemplateBizType, IImportHandler> handlerMap = new HashMap<>();
    private static Map<ImportTemplateBizType, ImportTemplateConfigDTO> configMap = new HashMap<>();

    public void doImport(MultipartFile uploadFile, ImportTemplateBizType type) {
        if (CollUtil.isEmpty(importHandlerList)
                || Objects.isNull(type) || Objects.isNull(uploadFile)) {
            return;
        }
        IImportHandler currentHandler = ImportFactory.handlerMap.get(type);
        ImportTemplateConfigDTO currentConfig = ImportFactory.configMap.get(type);
        if (Objects.isNull(currentHandler)) {
            log.warn("[{}]对应枚举没有实现类", type);
            return;
        }
        List dataList = null;
        try {
            dataList = EasyExcel.read(uploadFile.getInputStream())
                    .head(currentConfig.getClazz())
                    .sheet(0)
                    .headRowNumber(currentConfig.getHeadRowNum())
                    .doReadSync();
        } catch (Exception e) {
            log.error("导入文件解析失败", e);
            return;
        }
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        if (BooleanUtils.isTrue(currentHandler.isCheckParam())) {
            String error = this.validateParam(dataList, currentConfig);
            if (StrUtil.isNotBlank(error)) {
                throw new BusinessException(ResultEnum.SYS_ERROR, error);
            }
        }
        currentHandler.handleData(dataList);
    }

    /**
     * 校验参数
     * @param dataList 解析excel获取到的列表
     */
    private String validateParam(List dataList, ImportTemplateConfigDTO config) {
        int idx = config.getHeadRowNum() + 1;
        StringBuilder error = new StringBuilder();
        for (Object object : dataList) {
            List<String> errorList = ValidateUtil.valid(object);
            if (CollUtil.isNotEmpty(errorList)) {
                error.append("第").append(idx).append("行：").append(StringUtils.join(errorList, ";"));
            }
            idx ++;
        }
        return error.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (IImportHandler handler : importHandlerList) {
            ImportFactory.handlerMap.put(handler.type(), handler);
            if (Objects.nonNull(handler)) {
                ImportFactory.configMap.put(handler.type(), handler.buildConfig());
            }
        }
    }
}
