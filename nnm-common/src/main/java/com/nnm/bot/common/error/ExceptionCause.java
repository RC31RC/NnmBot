package com.nnm.bot.common.error;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/** 只去掉已知的异步/代理包装，保留业务或系统异常的语义根。 */
public final class ExceptionCause {
    private ExceptionCause() {}

    /** 返回调用方应处理的异常，不沿任意 cause 链一路剥离。
     *
     * @param failure 捕获到的异常
     * @return 去除已知异步或代理包装后的异常；没有可拆包装时返回原异常
     */
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
