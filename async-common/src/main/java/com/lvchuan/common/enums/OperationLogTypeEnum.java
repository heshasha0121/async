package com.lvchuan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lvchuan.common.mongo.MongoEnumData;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lvchuan
 * @description 日志业务操作类型
 * @date 2023/10/27 15:38:49
 */
@AllArgsConstructor
@Getter
@MongoEnumData
public enum OperationLogTypeEnum implements BaseEnum {
    /**
     * 作废
     */
    INVALID(1, "作废"),
    /**
     * 更新
     */
    UPDATE(2, "更新"),
    /**
     * 新增
     */
    ADD(3, "新增"),
    /**
     * 删除
     */
    DELETE(4, "删除");

    @EnumValue
    private Integer code;
    private String desc;
}
