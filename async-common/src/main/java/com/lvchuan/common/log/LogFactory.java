package com.lvchuan.common.log;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.lvchuan.common.enums.BaseEnum;
import com.lvchuan.common.enums.OperationBizTypeEnum;
import com.lvchuan.common.enums.OperationLogTypeEnum;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 日志处理工厂
 * @author: lvchuan
 * @createTime: 2024-07-18 10:22
 */
public class LogFactory {
    /**
     * 日志信息
     */
    private String message;

    /**
     * 忽略比较的字段
     */
    private final List<String> ignoreList = Arrays.asList("isDelete", "tenantCode", "gmtCreate",
            "gmtModified", "createById", "createBy", "lastUpdateById", "lastUpdateBy");

    public LogFactory(OperationBizTypeEnum bizType, OperationLogTypeEnum logType) {
        switch (logType) {
            case UPDATE: this.message = "修改"; break;
            case ADD: this.message = "新增"; break;
            default: break;
        }
        if (Objects.nonNull(bizType)) {
            this.message += "【" + bizType.getDesc() + "】信息";
        }
    }

    /**
     * 构建修改日志信息
     * @param oldObj 就是数据
     * @param newObj 新数据
     * @return
     */
    public LogFactory updateMsg(Object oldObj, Object newObj, List<String> showField) {
        if (Objects.isNull(oldObj) || Objects.isNull(newObj)) {
            return this;
        }
        if (oldObj instanceof Collection || newObj instanceof Collection) {
            return this;
        }
        Class oldClass = oldObj.getClass();
        Field[] oldFieldArr = ReflectUtil.getFieldsDirectly(oldClass, false);
        if (Objects.isNull(oldFieldArr) || oldFieldArr.length == 0) {
            return this;
        }
        if (CollUtil.isNotEmpty(showField)) {
            this.message += "【" + this.buildShowField(newObj, showField) + "】";
        }
        for (Field oldField : oldFieldArr) {
            String fieldName = oldField.getName();
            if (ignoreList.contains(fieldName)) {
                continue;
            }
            Object oldValue = ReflectUtil.getFieldValue(oldObj, oldField);
            Object newValue = ReflectUtil.getFieldValue(newObj, fieldName);
            if (oldValue instanceof String) {
                if (StrUtil.equals(String.valueOf(oldValue), String.valueOf(newValue))) {
                    continue;
                }
            } else {
                if (Objects.equals(oldValue, newValue)) {
                    continue;
                }
            }
            ApiModelProperty apiModelProperty = oldField.getAnnotation(ApiModelProperty.class);
            if (Objects.isNull(apiModelProperty)) {
                continue;
            }
            this.message += "," + apiModelProperty.value() + ":"
                    + this.handleFieldValue(oldValue) + "->" + this.handleFieldValue(newValue);
        }
        return this;
    }

    /**
     * 新增信息
     *
     * @param newObj 新增数据
     * @param showFieldList 显示字段
     * @return
     */
    public LogFactory addMsg(Object newObj, List<String> showFieldList) {
        if (Objects.isNull(newObj) || CollUtil.isEmpty(showFieldList)) {
            return this;
        }
        if (newObj instanceof Collection) {
            return this;
        }
        Class newClass = newObj.getClass();
        Field[] newFieldArr = ReflectUtil.getFields(newClass);
        if (Objects.isNull(newFieldArr) || newFieldArr.length == 0) {
            return this;
        }

        this.message += "【" + this.buildShowField(newObj, showFieldList) + "】";
        return this;
    }

    /**
     * 构建显示字段
     * @param newObj
     * @param showFieldList
     * @return
     */
    private String buildShowField(Object newObj, List<String> showFieldList) {
        List<String> msgList = new ArrayList<>();
        for (String fieldName : showFieldList) {
            Object newValue = ReflectUtil.getFieldValue(newObj, fieldName);
            if (Objects.isNull(newValue)) {
                continue;
            }
            msgList.add(String.valueOf(newValue));
        }
        if (CollUtil.isEmpty(msgList)) {
            return "";
        }
        return msgList.stream().collect(Collectors.joining("|"));
    }

    /**
     * 处理字段值
     * @param fieldValue
     * @return
     */
    private String handleFieldValue(Object fieldValue) {
        if (fieldValue == null) {
            return "无";
        }
        if (fieldValue instanceof BaseEnum) {
            return ((BaseEnum) fieldValue).getDesc();
        }

        return String.valueOf(fieldValue);
     }

    /**
     * 返回拼接后的信息,最多197字符，否则会被截断
     * @return
     */
    public String msg(){
        int truncateLent = 200 - 3;
        if (StrUtil.isNotBlank(this.message) && this.message.length() > truncateLent) {
            return this.message.substring(0, truncateLent) + "...";
        }
        return this.message;
     }
}
