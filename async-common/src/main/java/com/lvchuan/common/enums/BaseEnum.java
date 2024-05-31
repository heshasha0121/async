package com.lvchuan.common.enums;

/**
 * @author lvchuan
 * @description 基础枚举类型
 * @date 2023/7/31 17:46:11
 */
public interface BaseEnum {
    /**
     * 获取枚举编码
     *
     * @return 编码
     */
    Integer getCode();

    /**
     * 获取枚举描述
     *
     * @return
     */
    String getDesc();
}
