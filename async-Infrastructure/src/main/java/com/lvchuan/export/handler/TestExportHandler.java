package com.lvchuan.export.handler;

import cn.hutool.core.date.DateUtil;
import com.lvchuan.export.ExportTemplateBizTypeEnum;
import com.lvchuan.export.IDynamicExportHandler;
import com.lvchuan.model.ExportTemplateConfig;
import com.lvchuan.model.QueryPageReq;
import com.lvchuan.model.TestExportModel;
import com.lvchuan.common.page.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description: 任务处理器
 * @author: lvchuan
 * @createTime: 2023-12-08 10:41
 */
@Component
@Slf4j
public class TestExportHandler implements IDynamicExportHandler<QueryPageReq> {

    private static final String FILE_NAME = "文件信息导出";

    @Override
    public ExportTemplateBizTypeEnum bizType() {
        return ExportTemplateBizTypeEnum.TEST;
    }

    @Override
    public String fileName(QueryPageReq param) {
        Date currentDate = new Date();
        return FILE_NAME + "-" + DateUtil.format(currentDate, "yyyyMMdd") + "_" + currentDate.getTime();
    }

    @Override
    public LinkedHashMap<String, String> headMap(QueryPageReq param) {
        /**
         * headClass 或 headMap必须实现其中一个方法，同时实现优先用headMap方法的表头
         */
        //todo 获取自定义表头信息
        List<ExportTemplateConfig> configList = new ArrayList<>();
        LinkedHashMap<String, String> headMap = new LinkedHashMap<>();
        for (ExportTemplateConfig temp : configList) {
            //fieldName 字段名称与实体对齐，显示名称暂时不用管，主要与配置项相关，这里只有导出逻辑
            headMap.put(temp.getFieldName(), temp.getShowName());
        }
        return headMap;
    }

    @Override
    public Class headClass(QueryPageReq param) {
        /**
         * headClass 或 headMap必须实现其中一个方法，同时实现优先用headMap方法的表头
         * 这里返回class即可，会根据easy excel默认的方式去处理表头
         */
        return TestExportModel.class;
    }

    @Override
    public List<TestExportModel> queryData(QueryPageReq param, PageRequest pageRequest) {
        /**
         * 这里是具体业务查询数据的实现
         * param 为查询条件
         * pageRequest 为分页查询参数，主要传入当前页和每页查询条数
         * return 查询出来的数据，如果为空，将生成一个空的excel（包含表头）。循环分页查询直至没有数据为止
         */
        return new ArrayList<>();
    }
}
