package com.lvchuan.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.lvchuan.common.aysnc.AsyncBizCode;
import com.lvchuan.common.aysnc.AsyncEnable;
import com.lvchuan.common.aysnc.AsyncProxyUtil;
import com.lvchuan.common.enums.OperationBizTypeEnum;
import com.lvchuan.common.enums.OperationLogTypeEnum;
import com.lvchuan.common.log.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lvchuan
 * @description 日志服务
 * @date 2023/10/27 15:36:19
 */
@Service
@AsyncEnable
public class LogService {
    @Resource
    private AsyncProxyUtil asyncProxyUtil;

    /**
     * 保存日志信息
     *
     * @param logType 日志类型
     * @param bizId 业务编号
     * @param reason 原因
     * @param bizType 业务单据类型
     * @param message 操作内容
     */
    public void saveLogInfo(OperationLogTypeEnum logType, Long bizId,
                            OperationBizTypeEnum bizType,
                            String reason, String message) {
        this.saveLogInfo(logType, bizId, bizType, reason, message, new Date());
    }

    public void saveLogInfo(OperationLogTypeEnum logType, Long bizId,
                            OperationBizTypeEnum bizType,
                            String reason, String message, Date operateDate) {
        //入库
    }

    /**
     * 更新数据时保存日志，需要long类型 id字段
     * @param bizType
     * @param oldObj
     * @param newObj
     * @param showField 显示的特征字段
     */
    public void saveLogOfUpdate(OperationBizTypeEnum bizType, Object oldObj, Object newObj, List<String> showField) {
        if (Objects.isNull(oldObj)) {
            return;
        }
        Object id = ReflectUtil.getFieldValue(oldObj, "id");
        if (id instanceof Long) {
            asyncProxyUtil.proxy(this).asyncSaveLogOfUpdate((long) id, bizType, oldObj, newObj, new Date(), showField);
        }
    }

    /**
     * 新增时批量保存日志
     *
     * @param bizType 业务类型
     * @param newObjList 新增数据
     * @param showField  需要显示的数据
     */
    public void saveBatchLogOfAdd(OperationBizTypeEnum bizType, List newObjList, List<String> showField) {
        if (CollUtil.isEmpty(newObjList)) {
            return;
        }
        Date currentDate = new Date();
        for (Object newObj : newObjList) {
            Object id = ReflectUtil.getFieldValue(newObj, "id");
            if (id instanceof Long) {
                asyncProxyUtil.proxy(this).asyncSaveLogOfAdd((long) id, bizType, newObj, showField, currentDate);
            }
        }
    }

    /**
     *
     * @param bizType 业务类型
     * @param newObj 新增数据
     * @param showField  需要显示的数据
     */
    public void saveLogOfAdd(OperationBizTypeEnum bizType, Object newObj, List<String> showField) {
        if (Objects.isNull(newObj)) {
            return;
        }
        Object id = ReflectUtil.getFieldValue(newObj, "id");
        if (id instanceof Long) {
            asyncProxyUtil.proxy(this).asyncSaveLogOfAdd((long) id, bizType, newObj, showField, new Date());
        }
    }

    /**
     * 异步新增时保存日志
     * @param id
     * @param bizType
     * @param newObject
     * @param showField
     */
    @AsyncEnable("新增时保存日志")
    public void asyncSaveLogOfAdd(@AsyncBizCode Long id, OperationBizTypeEnum bizType, Object newObject,
                                  List<String> showField, Date operationTime) {
        String msg = this.factory(bizType, OperationLogTypeEnum.UPDATE).addMsg(newObject, showField).msg();
        this.saveLogInfo(OperationLogTypeEnum.ADD, id, bizType, null, msg, operationTime);
    }

    /**
     * 保存修改时的日志信息
     * @param bizId 业务id
     * @param bizType 类型
     * @param oldObj 旧数据
     * @param newObj 新数据
     */
    @AsyncEnable("异步保存修改日志信息")
    public void asyncSaveLogOfUpdate(@AsyncBizCode Long bizId, OperationBizTypeEnum bizType,
                                    Object oldObj, Object newObj, Date operationTime , List<String> showField) {
        String msg = this.factory(bizType, OperationLogTypeEnum.UPDATE).updateMsg(oldObj, newObj, showField).msg();
        this.saveLogInfo(OperationLogTypeEnum.UPDATE, bizId, bizType, null, msg, operationTime);
    }

    /**
     * 业务类型
     * @return
     */
    public LogFactory factory(OperationBizTypeEnum bizType, OperationLogTypeEnum logType) {
        return new LogFactory(bizType, logType);
    }
}
