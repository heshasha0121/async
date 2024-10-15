package com.lvchuan.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvchuan.common.aysnc.AsyncBizCode;
import com.lvchuan.common.aysnc.AsyncEnable;
import com.lvchuan.common.aysnc.AsyncProxyUtil;
import com.lvchuan.common.enums.OperationBizTypeEnum;
import com.lvchuan.common.enums.OperationLogTypeEnum;
import com.lvchuan.common.exception.BusinessException;
import com.lvchuan.common.log.LogFactory;
import com.lvchuan.common.web.ResultEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private final static int LIMIT_SIZE = 20000;

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
        this.saveLogInfo(logType, bizId, bizType, reason, message, new Date(), false);
    }

    /**
     * 保存日志信息
     *
     * @param logType 日志类型
     * @param bizId 业务编号
     * @param bizType 业务单据类型
     * @param message 操作内容
     */
    public void saveLogInfo(OperationLogTypeEnum logType, Long bizId,
                            OperationBizTypeEnum bizType, String message, Date operationTime) {
        this.saveLogInfo(logType, bizId, bizType, null, message, operationTime, false);
    }

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
                            String reason, String message,
                            Date operationTime, Boolean isDetail) {
        if (Objects.isNull(isDetail)) {
            isDetail = false;
        }
        if (Objects.isNull(bizId)) {
            throw new BusinessException(ResultEnum.SYS_ERROR, "日志业务编号不能为空");
        }
        if (Objects.isNull(logType)) {
            throw new BusinessException(ResultEnum.SYS_ERROR, "日志类型不能为空");
        }
        if (Objects.isNull(bizType)) {
            throw new BusinessException(ResultEnum.SYS_ERROR, "日志业务类型不能为空");
        }
        //todo 业务记录日志入库

    }

    /**
     * 更新数据时保存日志，需要long类型 id字段
     * @param bizType
     * @param oldObj
     * @param newObj
     */
    public void saveLogOfUpdate(OperationBizTypeEnum bizType, Object oldObj, Object newObj) {
        if (Objects.isNull(oldObj)) {
            return;
        }
        Object id = ReflectUtil.getFieldValue(oldObj, "id");
        if (id instanceof Long) {
            asyncProxyUtil.proxy(this).asyncSaveLogOfUpdate((long) id, bizType, oldObj, newObj, new Date(), false);
        }
    }

    public void saveLogOfInvalid(OperationBizTypeEnum bizType, Object oldObj, String reason) {
        Object id = ReflectUtil.getFieldValue(oldObj, "id");
        if (id instanceof Long) {
            this.saveLogOfInvalid(bizType, (long) id, oldObj, reason);
        }
    }

    public void saveLogOfInvalid(OperationBizTypeEnum bizType, Long bizId, Object oldObj, String reason) {
        if (Objects.isNull(oldObj)) {
            return;
        }
        asyncProxyUtil.proxy(this).asyncSaveLogOfInvalid(bizId, bizType, oldObj, new Date(), reason, false);
    }

    /**
     * 保存细单日志
     * @param bizType
     * @param bizId
     * @param oldList
     * @param newList
     */
    public void saveLogOfUpdateDetailList(OperationBizTypeEnum bizType, Long bizId, List oldList, List newList) {
        this.saveLogOfUpdateList(bizType, bizId, oldList, newList, true);
    }

    /**
     * 保存列表日志
     * @param bizType
     * @param bizId
     * @param oldList
     * @param newList
     */
    public void saveLogOfUpdateList(OperationBizTypeEnum bizType, Long bizId, List oldList, List newList, Boolean isDetail) {
        if (CollUtil.isEmpty(oldList) && CollUtil.isEmpty(newList)) {
            return;
        }
        if (CollUtil.isEmpty(oldList)) {
            //全部新增
            for (Object newObj : newList) {
                Object id = ReflectUtil.getFieldValue(newObj, "id");
                if (id instanceof Long) {
                    asyncProxyUtil.proxy(this).asyncSaveLogOfAdd(bizId, bizType, newObj, new Date(), isDetail);
                }
            }
            return;
        }
        if (CollUtil.isEmpty(newList)) {
            //全部删除
            for (Object oldObj : oldList) {
                Object id = ReflectUtil.getFieldValue(oldObj, "id");
                if (id instanceof Long) {
                    asyncProxyUtil.proxy(this).asyncSaveLogOfDelete(bizId, bizType, oldObj, new Date(), isDetail);
                }
            }
            return;
        }
        for (Object newObj : newList) {
            Object newId = ReflectUtil.getFieldValue(newObj, "id");
            boolean equalsFlag = false;
            for (Object oldObj : oldList) {
                Object oldId = ReflectUtil.getFieldValue(oldObj, "id");
                if (Objects.equals(newId, oldId)) {
                    asyncProxyUtil.proxy(this).asyncSaveLogOfUpdate(bizId, bizType, oldObj, newObj, new Date(), isDetail);
                    equalsFlag = true;
                    break;
                }
            }
            if (BooleanUtils.isFalse(equalsFlag)) {
                //新的有，旧的没有，算新增
                asyncProxyUtil.proxy(this).asyncSaveLogOfAdd(bizId, bizType, newObj, new Date(), isDetail);
            }
        }
        for (Object oldObj : oldList) {
            Object oldId = ReflectUtil.getFieldValue(oldObj, "id");
            boolean equalsFlag = false;
            for (Object newObj : newList) {
                Object newId = ReflectUtil.getFieldValue(newObj, "id");
                if (Objects.equals(newId, oldId)) {
                    equalsFlag = true;
                    break;
                }
            }
            if (BooleanUtils.isFalse(equalsFlag)) {
                //旧的有，新的没有，算删除
                asyncProxyUtil.proxy(this).asyncSaveLogOfDelete(bizId, bizType, oldObj, new Date(), isDetail);
            }
        }
    }

    /**
     * 更新数据时保存日志，传业务id
     * @param bizType
     * @param bizId
     * @param oldObj
     * @param newObj
     */
    public void saveLogOfUpdate(OperationBizTypeEnum bizType, Long bizId, Object oldObj, Object newObj) {
        if (Objects.isNull(oldObj)) {
            return;
        }
        asyncProxyUtil.proxy(this).asyncSaveLogOfUpdate(bizId, bizType, oldObj, newObj, new Date(), false);
    }

    /**
     * 保存明细日志
     * @param bizType
     * @param bizId
     * @param oldObj
     * @param newObj
     */
    public void saveLogDetailOfUpdate(OperationBizTypeEnum bizType, Long bizId, Object oldObj, Object newObj) {
        if (Objects.isNull(oldObj)) {
            return;
        }
        asyncProxyUtil.proxy(this).asyncSaveLogOfUpdate(bizId, bizType, oldObj, newObj, new Date(), true);
    }

    /**
     * 记录删除日志
     * @param obj 数据信息
     * @param bizType
     */
    public void saveLogOfDelete(Object obj, OperationBizTypeEnum bizType) {
        if (Objects.isNull(obj)) {
            return;
        }
        Object id = ReflectUtil.getFieldValue(obj, "id");
        if (id instanceof Long) {
            this.saveLogOfDelete((long) id, obj, bizType, false);
        }
    }

    /**
     * 记录删除日志
     * @param obj 数据信息
     * @param bizType
     */
    public void saveLogOfDelete(Long id, Object obj, OperationBizTypeEnum bizType, Boolean isDetail) {
        asyncProxyUtil.proxy(this).asyncSaveLogOfDelete(id, bizType, obj, new Date(), isDetail);
    }

    /**
     * 新增时批量保存日志
     *
     * @param bizType 业务类型
     * @param newObjList 新增数据
     */
    public void saveBatchLogOfAdd(OperationBizTypeEnum bizType, List newObjList) {
        if (CollUtil.isEmpty(newObjList)) {
            return;
        }
        Date currentDate = new Date();
        for (Object newObj : newObjList) {
            Object id = ReflectUtil.getFieldValue(newObj, "id");
            if (id instanceof Long) {
                asyncProxyUtil.proxy(this).asyncSaveLogOfAdd((long) id, bizType, newObj, currentDate, false);
            }
        }
    }

    /**
     *
     * @param bizType 业务类型
     * @param newObj 新增数据
     */
    public void saveLogOfAdd(OperationBizTypeEnum bizType, Object newObj) {
        if (Objects.isNull(newObj)) {
            return;
        }
        Object id = ReflectUtil.getFieldValue(newObj, "id");
        if (id instanceof Long) {
            this.saveLogOfAdd(bizType, (long) id, newObj, false);
        }
    }

    public void saveLogOfAdd(OperationBizTypeEnum bizType, Long id, Object newObj, Boolean isDetail) {
        asyncProxyUtil.proxy(this).asyncSaveLogOfAdd(id, bizType, newObj, new Date(), isDetail);
    }

    /**
     * 异步新增时保存日志
     * @param id
     * @param bizType
     * @param newObject
     */
    @AsyncEnable("新增时保存日志")
    public void asyncSaveLogOfAdd(@AsyncBizCode Long id, OperationBizTypeEnum bizType, Object newObject, Date operationTime, Boolean isDetail) {
        String msg = this.factory(bizType, OperationLogTypeEnum.ADD, isDetail).singleMsg(newObject).msg();
        this.saveLogInfo(OperationLogTypeEnum.ADD, id, bizType, null, msg, operationTime, isDetail);
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
                                     Object oldObj, Object newObj, Date operationTime, Boolean isDetail) {
        String msg = this.factory(bizType, OperationLogTypeEnum.UPDATE, isDetail).updateMsg(oldObj, newObj, isDetail).msg();
        this.saveLogInfo(OperationLogTypeEnum.UPDATE, bizId, bizType, null, msg, operationTime, isDetail);
    }

    @AsyncEnable("异步保存修改日志信息")
    public void asyncSaveLogOfInvalid(@AsyncBizCode Long bizId, OperationBizTypeEnum bizType,
                                      Object oldObj, Date operationTime,String reason, Boolean isDetail) {
        String msg = this.factory(bizType, OperationLogTypeEnum.INVALID, isDetail).singleMsg(oldObj).msg();
        this.saveLogInfo(OperationLogTypeEnum.INVALID, bizId, bizType, reason, msg, operationTime, isDetail);
    }

    @AsyncEnable("异步保存删除日志信息")
    public void asyncSaveLogOfDelete(@AsyncBizCode Long bizId, OperationBizTypeEnum bizType,
                                     Object obj, Date operationTime, Boolean isDetail) {
        String msg = this.factory(bizType, OperationLogTypeEnum.DELETE, isDetail).singleMsg(obj).msg();
        this.saveLogInfo(OperationLogTypeEnum.UPDATE, bizId, bizType, null, msg, operationTime, isDetail);
    }

    /**
     * 业务类型
     * @return
     */
    public LogFactory factory(OperationBizTypeEnum bizType, OperationLogTypeEnum logType, boolean isDetail) {
        return new LogFactory(bizType, logType, isDetail);
    }
}