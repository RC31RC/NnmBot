package com.nnm.bot.common.storage;

import com.nnm.bot.common.exception.SystemException;

public class ObjectStorageException extends SystemException {

    public ObjectStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
