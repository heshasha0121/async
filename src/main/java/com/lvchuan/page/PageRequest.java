package com.lvchuan.page;

import lombok.Data;

/**
 * @description: 分页
 * @author: lvchuan
 * @createTime: 2024-01-15 14:05
 */
@Data
public class PageRequest {
    private Integer size;
    private Integer current;
}
