package com.lvchuan.export;

import com.alibaba.excel.write.handler.WriteHandler;
import com.lvchuan.common.page.PageRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @description: 动态导出服务接口
 * @author: lvchuan
 * @createTime: 2023-12-06 10:56
 */
public interface IDynamicExportHandler<T> {
    /**
     * 导出业务类型
     * @return 业务类型
     */
    ExportTemplateBizTypeEnum bizType();

    /**
     * 文件名称， 不加后缀
     * @param param 传入的请求参数
     * @return 返回文件名称
     */
    default String fileName(T param) {
        return null;
    };

    /**
     * sheet 名称
     * @param param 传入的请求参数
     * @return 返回sheet 名称
     */
    default String sheetName(T param) {
        return null;
    }

    /**
     * 头部class文件
     *
     * @param param 传入的请求参数
     * @return 返回头设置
     */
    default Class headClass(T param) {
        return null;
    }

    /**
     * 动态文件头
     *
     * @param param 传入的请求参数 <字段名称, 表头显示的名称>
     * @return 文件头list
     */
    default LinkedHashMap<String, String> headMap(T param) {
        return null;
    }

    /**
     * 自定义处理器列表，依次加载
     *
     * @param param 传入的请求参数
     * @return 自定义处理器列表
     */
    default List<WriteHandler> writeHandlerList(T param) {
        return null;
    }

    /**
     * 查询数据
     *
     * @param param 传入的请求参数
     * @param pageRequest 分页信息
     * @return 查询出来的业务数据
     */
    List queryData(T param, PageRequest pageRequest);
}
