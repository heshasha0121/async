package com.lvchuan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lvchuan
 * @description 操作日志业务类型枚举
 * @date 2023/10/30 11:42:32
 */
@AllArgsConstructor
@Getter
public enum OperationBizTypeEnum implements BaseEnum {
    /**
     * 原料入库
     */
    RAW_MATERIAL_IN_WAREHOUSE(1, "原料入库"),

    RAW_MATERIAL_QUALITY_INSPECTION(2, "原料质检"),

    RAW_MATERIAL_PURCHASE(3, "原料采购"),
    RAW_MATERIAL_OUT_WAREHOUSE(4, "原料出库"),
    /**
     * 饮片质检
     */
    PIECES_QUALITY(5, "饮片质检"),

    /**
     * 饮片包装
     */
    PIECES_PACKAGE(6, "饮片包装"),

    /**
     * 饮片生产
     */
    PIECES_PRODUCE(7, "饮片生产"),

    RAW_MATERIAL(8, "原料模板"),
    ;

    @EnumValue
    private Integer code;
    private String desc;
}
