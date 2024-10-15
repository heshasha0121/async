package com.lvchuan.common.log;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import java.util.Objects;

/**
 * @description: 日志上下文
 * @author: lvchuan
 * @createTime: 2024-09-24 17:59
 */
public class LogContext {
    public static ThreadLocal<OpLocalDTO> LOG_OP_LOCAL = new ThreadLocal();

    public static void create() {
        OpLocalDTO opLocal = new OpLocalDTO();
        opLocal.setOpId(IdWorker.getId());
        LOG_OP_LOCAL.set(opLocal);
    }

    public static void create(Long opId) {
        OpLocalDTO opLocal = new OpLocalDTO();
        if (Objects.isNull(opId)) {
            opId = IdWorker.getId();
        }
        opLocal.setOpId(opId);
        LOG_OP_LOCAL.set(opLocal);
    }

    public static Long getOpId() {
        OpLocalDTO opLocalDTO = LOG_OP_LOCAL.get();
        if (Objects.isNull(opLocalDTO)) {
            return null;
        }
        return opLocalDTO.getOpId();
    }
    public static void remove() {
        LOG_OP_LOCAL.remove();
    }
}
