package com.lvchuan.model;

import com.lvchuan.common.enums.OpenConvertIdTypeEnum;
import com.lvchuan.common.open.OpenConvertBean;
import com.lvchuan.common.open.OpenConvertId;
import lombok.Data;

/**
 * @description: 转换明细
 * @author: lvchuan
 * @createTime: 2024-11-07 18:02
 */
@OpenConvertBean
@Data
public class OpenDetailModel {
    @OpenConvertId(type = OpenConvertIdTypeEnum.TEST_DETAIL)
    private Long detailId;
}
