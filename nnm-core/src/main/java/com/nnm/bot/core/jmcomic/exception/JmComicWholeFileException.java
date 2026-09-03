package com.nnm.bot.core.jmcomic.exception;

/**
 * 整本下载快速路径不可用。调用方可以保留原因并回退到逐图下载。
 */
public class JmComicWholeFileException extends RuntimeException {

    public JmComicWholeFileException(String message) {
        super(message);
    }

    public JmComicWholeFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
