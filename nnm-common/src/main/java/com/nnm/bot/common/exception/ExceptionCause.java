package com.nnm.bot.common.exception;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/** 只去掉已知的异步/代理包装，保留业务或系统异常的语义根。 */
public final class ExceptionCause {
    private ExceptionCause() {}

    /** 返回调用方应处理的异常，不沿任意 cause 链一路剥离。 */
    public static Throwable unwrap(Throwable failure) {
        Throwable current = failure;
        while (current instanceof CompletionException || current instanceof ExecutionException
            || current instanceof UndeclaredThrowableException) {
            if (current.getCause() == null || current.getCause() == current)
                break;
            current = current.getCause();
        }
        return current;
    }
}
