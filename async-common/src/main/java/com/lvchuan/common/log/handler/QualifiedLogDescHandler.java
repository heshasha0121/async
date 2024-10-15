package com.lvchuan.common.log.handler;

import com.lvchuan.common.log.ILogDescHandler;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description: 启用停用日志描述处理
 * @author: lvchuan
 * @createTime: 2024-07-22 10:51
 */
@Component
public class QualifiedLogDescHandler implements ILogDescHandler<Boolean> {
    @Override
    public String buildMsg(Boolean object) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (BooleanUtils.isFalse(object)) {
            return "不合格";
        } else {
            return "合格";
        }
    }
}
