package com.nnm.bot.common.storage;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.constant.ObjectStorageErrorCode;

/** 对象存储边界的系统异常，内部原因仅用于日志诊断。 */
public class ObjectStorageException extends SystemException {

    /** 保留操作诊断及 SDK 根因。
     *
     * @param message 内部操作说明
     * @param cause SDK 或传输层原因
     */
    public ObjectStorageException(String message, Throwable cause) {
        super(ObjectStorageErrorCode.OPERATION_FAILED, message, cause);
    }
}
