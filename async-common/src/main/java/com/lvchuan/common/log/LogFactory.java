package com.lvchuan.common.log;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.lvchuan.common.enums.BaseEnum;
import com.lvchuan.common.enums.OperationBizTypeEnum;
import com.lvchuan.common.enums.OperationLogTypeEnum;
import com.lvchuan.common.log.ILogDescHandler;
import com.lvchuan.common.log.LogDesc;
import com.lvchuan.common.log.LogIgnoreField;
import com.lvchuan.common.log.LogShowField;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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

    public static Map<String, ILogDescHandler> logDescHandlerMap = new HashMap<>();

    public LogFactory(OperationBizTypeEnum bizType, OperationLogTypeEnum logType, boolean isDetail) {
        if (isDetail && Objects.equals(logType, OperationLogTypeEnum.UPDATE)) {
            return;
        }
        switch (logType) {
            case UPDATE: this.message = "修改"; break;
            case ADD: this.message = "新增"; break;
            case DELETE: this.message = "删除"; break;
            case INVALID: this.message = "作废"; break;
            default: break;
        }
        if (isDetail) {
            return;
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
    public LogFactory updateMsg(Object oldObj, Object newObj, boolean isDetail) {
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
        if (!isDetail) {
            String fieldMsg = this.buildShowField(newObj);
            if (StrUtil.isNotBlank(fieldMsg)) {
                this.message += "【" + fieldMsg + "】";
            }
        }
        for (Field oldField : oldFieldArr) {
            String fieldName = oldField.getName();
            if (ignoreList.contains(fieldName)) {
                continue;
            }
            boolean logIgnoreField = oldField.isAnnotationPresent(LogIgnoreField.class);
            if (BooleanUtils.isTrue(logIgnoreField)) {
                continue;
            }
            Object oldValue = ReflectUtil.getFieldValue(oldObj, oldField);
            Object newValue = ReflectUtil.getFieldValue(newObj, fieldName);
            Field newField = ReflectUtil.getField(newObj.getClass(), fieldName);
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
            String updateMsg = apiModelProperty.value() + ":"
                    + this.handleFieldValue(oldValue, oldField, oldObj)
                    + "->"
                    + this.handleFieldValue(newValue, newField, newObj);
            if (Objects.isNull(this.message)) {
                this.message = updateMsg;
            } else {
                this.message += "," + updateMsg;
            }
        }
        return this;
    }

    /**
     * 新增信息
     *
     * @param newObj 新增数据
     * @return
     */
    public LogFactory singleMsg(Object newObj) {
        if (Objects.isNull(newObj)) {
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
        String filedMsg = this.buildShowField(newObj);
        if (StrUtil.isNotBlank(filedMsg)) {
            this.message += "【" + filedMsg + "】";
        }
        return this;
    }

    /**
     * 构建显示字段
     * @param obj
     * @return
     */
    private String buildShowField(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        List<String> showFieldList = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean bool = field.isAnnotationPresent(LogShowField.class);
            if (BooleanUtils.isTrue(bool)) {
                showFieldList.add(field.getName());
            }
        }
        if (CollUtil.isEmpty(showFieldList)) {
            return null;
        }
        List<String> msgList = new ArrayList<>();
        for (String fieldName : showFieldList) {
            Object newValue = ReflectUtil.getFieldValue(obj, fieldName);
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
    private String handleFieldValue(Object fieldValue, Field field, Object data) {
        if (fieldValue == null) {
            return "无";
        }
        LogDesc logDesc = field.getAnnotation(LogDesc.class);
        if (Objects.nonNull(logDesc)) {
            Class logDescClass = logDesc.value();
            ILogDescHandler logDescHandler = logDescHandlerMap.get(logDescClass.getName());
            if (Objects.nonNull(logDescHandler)) {
                String paramFiledName = logDesc.paramFiledName();
                if (StrUtil.isNotBlank(paramFiledName)) {
                    fieldValue = ReflectUtil.getFieldValue(data, paramFiledName);
                }
                return logDescHandler.buildMsg(fieldValue);
            }
        }
        if (fieldValue instanceof BaseEnum) {
            return ((BaseEnum) fieldValue).getDesc();
        }
        if (fieldValue instanceof BigDecimal) {
            return ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
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