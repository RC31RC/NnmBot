package com.nnm.bot.core.jmcomic.exception;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.core.jmcomic.constant.JmComicErrorCode;

/**
 * 整本下载快速路径不可用。调用方可以保留原因并回退到逐图下载。
 */
public class JmComicWholeFileException extends SystemException {

    public JmComicWholeFileException(String message) {
        super(JmComicErrorCode.WHOLE_FILE_FALLBACK_REQUIRED, message);
    }

    public JmComicWholeFileException(String message, Throwable cause) {
        super(JmComicErrorCode.WHOLE_FILE_FALLBACK_REQUIRED, message, cause);
    }
}
