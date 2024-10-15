package com.lvchuan.common.log.handler;

import cn.hutool.core.date.DateUtil;
import com.lvchuan.common.log.ILogDescHandler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @description: yyyy-MM-dd日期格式
 * @author: lvchuan
 * @createTime: 2024-07-22 16:27
 */
@Component
public class NormDateLogDescHandler implements ILogDescHandler<Date> {
    @Override
    public String buildMsg(Date object) {
        if (Objects.isNull(object)) {
            return "";
        }
        return DateUtil.formatDate(object);
    }
}
