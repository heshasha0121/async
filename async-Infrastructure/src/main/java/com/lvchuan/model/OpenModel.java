package com.lvchuan.model;

import com.lvchuan.common.enums.OpenConvertIdTypeEnum;
import com.lvchuan.common.open.OpenConvertBean;
import com.lvchuan.common.open.OpenConvertId;
import lombok.Data;

import java.util.List;

/**
 * @description: 转换实体
 * @author: lvchuan
 * @createTime: 2024-11-07 18:01
 */
@Data
@OpenConvertBean
public class OpenModel {
    @OpenConvertId(type = OpenConvertIdTypeEnum.TEST)
    private Long id;

    @OpenConvertBean
    private List<OpenDetailModel> detailList;

    @OpenConvertBean
    private OpenDetailModel detail;
}
