package com.lvchuan.common.canal.config;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import top.javatool.canal.client.enums.TableNameEnum;
import top.javatool.canal.client.factory.IModelFactory;
import top.javatool.canal.client.handler.EntryHandler;
import top.javatool.canal.client.util.EntryUtil;
import top.javatool.canal.client.util.FieldUtil;
import top.javatool.canal.client.util.GenericUtil;
import top.javatool.canal.client.util.HandlerUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * @description: 自定义数据转换工厂
 * @author: lvchuan
 * @createTime: 2024-08-23 11:14
 */
public class DataMapColumnModelFactory implements IModelFactory<Map<String, String>> {

    public DataMapColumnModelFactory() {
    }

    @Override
    public <R> R newInstance(EntryHandler entryHandler, Map<String, String> t) throws Exception {
        String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
        if (TableNameEnum.ALL.name().toLowerCase().equals(canalTableName)) {
            return (R) t;
        } else {
            Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
            return tableClass != null ? this.newInstance(tableClass, t) : null;
        }
    }

    /**
     * 字段转换实现
     * @param c
     * @param valueMap
     * @return
     * @param <R>
     * @throws Exception
     */
    public <R> R newInstance(Class<R> c, Map<String, String> valueMap) throws Exception {
        R object = c.newInstance();
        Map<String, String> columnNames = EntryUtil.getFieldName(object.getClass());
        Iterator var5 = valueMap.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var5.next();
            String fieldName = this.getFieldName(columnNames, entry);
            if (StringUtils.isNotEmpty(fieldName)) {
                FieldUtil.setFieldValue(object, fieldName, (String)entry.getValue());
            }
        }

        return object;
    }

    /**
     * 获取字段名称，主要是下划线转驼峰命名法，由于实体和表字段明明方式可能不一致
     * @param columnNames
     * @param entry
     * @return
     */
    private String getFieldName(Map<String, String> columnNames, Map.Entry<String, String> entry) {
        String entryKey = entry.getKey();
        String fileName = columnNames.get(entryKey);
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        if (entryKey.contains("_")) {
            entryKey = StrUtil.toCamelCase(entryKey);
        }
        return columnNames.get(entryKey);
    }
}
