package com.lvchuan.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lvchuan.enums.BaseEnumDeserializer;
import com.lvchuan.enums.BaseEnumSerializer;
import com.lvchuan.export.ExportTemplateBizTypeEnum;
import lombok.Data;

/**
 * @description: 导出服务请求参数
 * @author: lvchuan
 * @createTime: 2023-12-10 10:05
 */
@Data
public class ExportReq {
    /**
     * 业务类型
     */
    @JsonDeserialize(using = BaseEnumDeserializer.class)
    @JsonSerialize(using = BaseEnumSerializer.class)
    private ExportTemplateBizTypeEnum bizType;

    /**
     * 查询参数
     */
    private JSONObject param;
}
