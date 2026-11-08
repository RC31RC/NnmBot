package com.nnm.bot.core.plugin.execution.advice;

import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 注册命令执行异常的回复映射；不参与 HTTP 异常处理或事务回滚。 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public @interface BotExceptionAdvice {
    /** 同一异常类型的处理优先级，数值越小越优先。 */
    @AliasFor(annotation = Order.class, attribute = "value")
    int order() default Ordered.LOWEST_PRECEDENCE;
}
