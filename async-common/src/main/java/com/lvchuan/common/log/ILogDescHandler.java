package com.lvchuan.common.log;

/**
 * @description: 日志描述接口
 * @author: lvchuan
 * @createTime: 2024-07-22 10:46
 */
public interface ILogDescHandler<T>{
    /**
     * 构建描述信息
     * @param
     * @return
     */
    String buildMsg(T object);
}
