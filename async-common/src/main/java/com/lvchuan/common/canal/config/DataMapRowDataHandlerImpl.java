package com.lvchuan.common.canal.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.otter.canal.protocol.CanalEntry;
import top.javatool.canal.client.factory.IModelFactory;
import top.javatool.canal.client.handler.EntryHandler;
import top.javatool.canal.client.handler.RowDataHandler;

import java.util.List;
import java.util.Map;

/**
 * @description: 自定义数据解析器
 * @author: lvchuan
 * @createTime: 2024-08-23 10:56
 */
public class DataMapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {
    private IModelFactory<Map<String, String>> modelFactory;

    public DataMapRowDataHandlerImpl(IModelFactory<Map<String, String>> modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * 自定义数据处理
     * @param list
     * @param entryHandler
     * @param eventType
     * @param <R>
     * @throws Exception
     */
    @Override
    public <R> void handlerRowData(List<Map<String, String>> list, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) throws Exception {
        if (entryHandler != null) {
            switch (eventType) {
                case INSERT:
                    R entry = this.modelFactory.newInstance(entryHandler, list.get(0));
                    entryHandler.insert(entry);
                    break;
                case UPDATE:
                    Map<String, String> afterMap = list.get(0);
                    Map<String, String> beforeMap = this.buildAllBeforeMap(afterMap, list.get(1));
                    R before = this.modelFactory.newInstance(entryHandler, beforeMap);
                    R after = this.modelFactory.newInstance(entryHandler, afterMap);
                    entryHandler.update(before, after);
                    break;
                case DELETE:
                    R o = this.modelFactory.newInstance(entryHandler, list.get(0));
                    entryHandler.delete(o);
            }
        }

    }

    /**
     * 构建完整的old数据
     * @param afterMap
     * @param beforeMap
     * @return
     */
    private Map<String, String> buildAllBeforeMap(Map<String, String> afterMap, Map<String, String> beforeMap) {
        if (CollUtil.isEmpty(afterMap) || CollUtil.isEmpty(beforeMap)) {
            return beforeMap;
        }
        for (Map.Entry<String, String> item : afterMap.entrySet()) {
            if (!beforeMap.containsKey(item.getKey())) {
                beforeMap.put(item.getKey(), item.getValue());
            }
        }
        return beforeMap;
    }

}
