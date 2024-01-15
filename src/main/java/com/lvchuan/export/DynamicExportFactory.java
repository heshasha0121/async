package com.lvchuan.export;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvchuan.aysnc.AsyncBizCode;
import com.lvchuan.aysnc.AsyncEnable;
import com.lvchuan.aysnc.AsyncProxyUtil;
import com.lvchuan.enums.AsyncExportTaskStatusEnum;
import com.lvchuan.export.style.HeadCellWriteWeightStyleHandler;
import com.lvchuan.model.AsyncExportTask;
import com.lvchuan.page.PageRequest;
import com.lvchuan.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @description: 动态导出工厂
 * @author: lvchuan
 * @createTime: 2023-12-07 13:49
 */
@Slf4j
@Component
@AsyncEnable
public class DynamicExportFactory {
    @Autowired
    private List<IDynamicExportHandler> dynamicExportHandlerList;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AsyncProxyUtil asyncProxyUtil;

    /**
     * 导出文件最大条数
     */
    @Value("${file.export.max.num: 500000}")
    private Integer fileExportMaxNum;

    /**
     * 获取导出处理类
     * @param bizType 业务类型
     * @return 请求参数
     */
    private IDynamicExportHandler getDynamicExportHandler(ExportTemplateBizTypeEnum bizType) {
        for (IDynamicExportHandler handler : dynamicExportHandlerList) {
            if (Objects.equals(bizType, handler.bizType())) {
                return handler;
            }
        }
        throw new RuntimeException("动态导出未获取到对应实现类");
    }

    /**
     * 获取文件名称
     * @param handler 业务处理器
     * @param param 参数
     * @return 文件名称
     */
    private String getFileName(IDynamicExportHandler handler, Object param) {
        String fileName = handler.fileName(param);
        Date currentDate = new Date();
        try {
            fileName = fileName + ".xlsx";
        } catch (Exception e) {
            log.error("动态导出转换文件名称错误:{},{}", fileName, e);
            throw new RuntimeException("动态导出转换文件名称错误");
        }
        return fileName;
    }

    /**
     * 构建sheet名称
     * @param writerBuilder 写入构造器
     * @param handler 业务处理器
     * @param param 请求参数
     * @return sheet类
     */
    private WriteSheet buildSheetName(ExcelWriterBuilder writerBuilder,
                                 IDynamicExportHandler handler,
                                 Object param) {
        String sheetName = handler.sheetName(param);
        if (StrUtil.isNotBlank(sheetName)) {
            writerBuilder.sheet(sheetName);
        } else {
            sheetName = "sheet";
        }
        return EasyExcel.writerSheet(sheetName).build();
    }

    /**
     * 构造表头
     * @param writerBuilder 写入构造器
     * @param handler 业务处理器
     * @param param 请求参数
     * @return 是否是自定义表头
     */
    private LinkedHashMap<String, String> buildHead(ExcelWriterBuilder writerBuilder,
                           IDynamicExportHandler handler,
                           Object param) {
        LinkedHashMap<String, String> headMap = handler.headMap(param);
        if (CollUtil.isNotEmpty(headMap)) {
            List<List<String>> head = new ArrayList<>();
            for(Map.Entry<String, String> entry : headMap.entrySet()) {
                List<String> headList = new ArrayList<>();
                headList.add(entry.getValue());
                head.add(headList);
            }
            writerBuilder.head(head);
            return headMap;
        } else {
            Class headClass = handler.headClass(param);
            if (Objects.isNull(headClass)) {
                throw new RuntimeException("请配置表头");
            }
            writerBuilder.head(headClass);
            return null;
        }
    }

    /**
     * 构建风格构造器
     *
     * @param writerBuilder 写入构造器
     * @param handler 业务处理器
     * @param param 参数
     */
    private void buildRegisterWriteHandler(ExcelWriterBuilder writerBuilder,
                                           IDynamicExportHandler handler,
                                           Object param) {
        List<WriteHandler> writeHandlerList = handler.writeHandlerList(param);
        if (CollUtil.isNotEmpty(writeHandlerList)) {
            for (WriteHandler temp : writeHandlerList) {
                writerBuilder.registerWriteHandler(temp);
            }
        }
        //添加默认分割配置
        //自适应宽度
        writerBuilder.registerWriteHandler(new HeadCellWriteWeightStyleHandler());
    }

    /**
     * 写入数据
     * @param excelWriter 写入处理类
     * @param writeSheet sheet类
     * @param handler 业务处理器
     * @param param 请求参数
     * @param customizeHeadMap 自定义表头
     */
    private void writeData(ExcelWriter excelWriter,
                           WriteSheet writeSheet,
                           IDynamicExportHandler handler,
                           Object param,
                           LinkedHashMap<String, String> customizeHeadMap) {
        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setSize(500);
            pageRequest.setCurrent(1);
            int dataSize = 0;
            while (true) {
                List<Object> dataList = handler.queryData(param, pageRequest);
                if (CollUtil.isEmpty(dataList)) {
                    if (pageRequest.getCurrent() == 1) {
                        //没有数据，写入空文件
                        excelWriter.write(null, writeSheet);
                    }
                    break;
                } else  {
                    dataSize += dataList.size();
                    if (dataSize > fileExportMaxNum) {
                        break;
                    }
                }
                if (CollUtil.isNotEmpty(customizeHeadMap)) {
                    excelWriter.write(this.customizeHeadBuildData(dataList, customizeHeadMap), writeSheet);
                } else {
                    excelWriter.write(dataList, writeSheet);
                }
                pageRequest.setCurrent(pageRequest.getCurrent() + 1);
            }
        } catch (Exception e) {
            log.error("导出数据失败:{}", e);
            throw new RuntimeException("导出数据失败");
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 自定义表头构造数据
     * @param customizeHeadMap 自定义表头配置
     * @param dataList 业务数据
     * @return 处理后的数据
     */
    private List<List<Object>> customizeHeadBuildData(List<Object> dataList,
                                                      LinkedHashMap<String, String> customizeHeadMap) throws IllegalAccessException {
        List<String> headFieldList = new ArrayList<>();
        for(Map.Entry<String, String> entry : customizeHeadMap.entrySet()) {
            headFieldList.add(entry.getKey());
        }
        List<List<Object>> result = new ArrayList<>();
        for (Object temp : dataList) {
            Field[] fieldList = temp.getClass().getDeclaredFields();
            Object[] currentDataList = new Object[headFieldList.size()];
            for (Field field : fieldList) {
                if (!headFieldList.contains(field.getName())) {
                    continue;
                }
                int idx = headFieldList.indexOf(field.getName());
                if (idx < 0 || idx > headFieldList.size() - 1) {
                    continue;
                }
                ReflectionUtils.makeAccessible(field);
                currentDataList[idx] = field.get(temp);
            }
            result.add(Arrays.asList(currentDataList));
        }
        return result;
    }

    /**
     * 构建临时文件路径
     * @return 临时文件路径
     */
    private String buildTempFilePath() {
        String dir = System.getProperty("user.dir");
        return dir;
    }

    /**
     * 同步导出
     * @param paramJson 请求参数
     * @param bizType 业务类型
     * @return 文件地址
     */
    public String syncExport(JSONObject paramJson, ExportTemplateBizTypeEnum bizType) {
        return this.export(paramJson, bizType);
    }

    /**
     * 同步导出
     * @param paramJson 请求参数
     * @param bizType 业务类型
     * @return 导出任务id
     */
    public Long asyncExport(JSONObject paramJson, ExportTemplateBizTypeEnum bizType) {
        Date currentDate = new Date();
        //todo 获取id策略
        Long id = 1L;
        AsyncExportTask asyncExportTask = new AsyncExportTask();
        asyncExportTask.setId(id);
        asyncExportTask.setStatus(AsyncExportTaskStatusEnum.WAIT);
        asyncExportTask.setBizType(bizType);
        asyncExportTask.setGmtCreate(currentDate);
        asyncExportTask.setTaskName(bizType.getDesc());
        asyncExportTask.setGmtModified(currentDate);
        //todo 保存异步导出信息
        asyncProxyUtil.proxy(this).dealAsyncExportTask(id, paramJson);
        return id;
    }

    /**
     * 处理异步导出任务
     * @param taskId 任务id
     * @param paramJson 参数
     */
    @AsyncEnable("异步导出任务")
    public void dealAsyncExportTask(@AsyncBizCode Long taskId, JSONObject paramJson) {
        //todo 查询数据库导出信息
        AsyncExportTask asyncExportTask = new AsyncExportTask();
        if (Objects.isNull(asyncExportTask)) {
            log.error("未获取到异步导出任务:{}", taskId);
            return;
        }
        String url;
        try {
            url = this.export(paramJson, asyncExportTask.getBizType());
            String fileName = null;
            if (StrUtil.isNotBlank(url)) {
                fileName = url.substring(url.lastIndexOf("/") + 1);
            }
            asyncExportTask.setFileUrl(url);
            asyncExportTask.setFileName(fileName);
            asyncExportTask.setStatus(AsyncExportTaskStatusEnum.FINISH);
        } catch (Exception e) {
            log.error("导出任务异常:{},{}", taskId, e);
            asyncExportTask.setStatus(AsyncExportTaskStatusEnum.ERROR);
        }
        asyncExportTask.setGmtModified(new Date());
        //todo 更新异步导出信息
    }

    /**
     * 获取业务参数
     * @param handler 处理器
     * @return 业务参数
     */
    private Object getParam(IDynamicExportHandler handler, JSONObject paramJson)
            throws JsonProcessingException {
        if (Objects.isNull(paramJson)) {
            return null;
        }
        Class objClass = ClassUtils.getGenericInterfacesClass(handler, 0);
        if (objClass == null) {
            return null;
        }
        return objectMapper.readValue(paramJson.toJSONString(), objClass);
    }

    /**
     * 导出主要逻辑
     */
    public String export(JSONObject paramJson, ExportTemplateBizTypeEnum bizType) {
        ExcelWriter excelWriter;
        File file = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            IDynamicExportHandler handler = this.getDynamicExportHandler(bizType);
            //获取请求参数
            Object param = this.getParam(handler, paramJson);
            //获取文件名称
            String fileName = this.getFileName(handler, param);
            //获取临时文件地址
            String dir = this.buildTempFilePath();
            file = FileUtil.file(dir, fileName);
            ExcelWriterBuilder writerBuilder = EasyExcel.write(file)
                    .autoCloseStream(Boolean.FALSE);
            //构造sheet
            WriteSheet writeSheet = this.buildSheetName(writerBuilder, handler, param);
            //构造表头
            LinkedHashMap<String, String> customizeHeadMap = this.buildHead(writerBuilder, handler, param);
            //构造自定义处理器
            this.buildRegisterWriteHandler(writerBuilder, handler, param);
            //创建导出类
            excelWriter = writerBuilder.build();
            //处理业务数据
            this.writeData(excelWriter, writeSheet, handler, param, customizeHeadMap);
            // 获取文件流
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            byte[] fileData = bos.toByteArray();
            // 文件服务，如oss等，返回文件地址
            return "https://123.jpg";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出文件出错");
        } finally {
            try {
                if (Objects.nonNull(fis)) {
                    fis.close();
                }
                if (Objects.nonNull(bos)) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(file)) {
                file.delete();
            }
        }
    }
}
