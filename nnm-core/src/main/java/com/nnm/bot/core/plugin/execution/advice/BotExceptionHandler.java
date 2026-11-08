package com.nnm.bot.core.plugin.execution.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 声明一个处理方法所接受的异常类型，方法只负责映射回复。 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotExceptionHandler {
    Class<? extends Exception>[] value();
}
