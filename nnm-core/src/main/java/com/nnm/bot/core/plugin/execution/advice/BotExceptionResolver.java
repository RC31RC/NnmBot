package com.nnm.bot.core.plugin.execution.advice;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** 启动时校验 Advice 注册，按最具体异常类型、同类型 Advice 顺序映射命令回复。 */
@Component
public class BotExceptionResolver {
    private final Map<Class<? extends Exception>, Handler> handlers;

    public BotExceptionResolver(ListableBeanFactory beans) {
        Map<Class<? extends Exception>, Handler> registered = new HashMap<>();
        Map<String, Object> adviceBeans = new HashMap<>(beans.getBeansWithAnnotation(BotExceptionAdvice.class));
        // JDK 代理的 bean 类型是接口，按 bean 注解查找时可能看不到目标类上的 Advice 注解。
        beans.getBeansOfType(Advised.class, false, false).forEach((name, proxy) -> {
            if (AnnotatedElementUtils.findMergedAnnotation(AopUtils.getTargetClass(proxy),
                BotExceptionAdvice.class) != null)
                adviceBeans.putIfAbsent(name, proxy);
        });
        adviceBeans.forEach((name, advice) -> {
            Class<?> target = AopUtils.getTargetClass(advice);
            int order = OrderUtils.getOrder(target, Ordered.LOWEST_PRECEDENCE);
            Map<Method,
                BotExceptionHandler> methods = MethodIntrospector.selectMethods(target,
                    (MethodIntrospector.MetadataLookup<BotExceptionHandler>)method -> AnnotatedElementUtils
                        .findMergedAnnotation(method, BotExceptionHandler.class));
            if (methods.isEmpty())
                throw invalid("BotExceptionAdvice has no handlers: " + target.getName());
            methods.forEach((method, annotation) -> register(registered, advice, method, annotation, order));
        });
        if (!registered.containsKey(Exception.class))
            throw invalid("BotExceptionAdvice must handle Exception as a fallback");
        handlers = Map.copyOf(registered);
    }

    /** 查找最具体的异常处理方法；回复与失败报告独立声明，均不影响已经结束的事务。 */
    public BotFailureDecision resolve(Exception failure) {
        Objects.requireNonNull(failure, "failure");
        for (Class<?> type = failure.getClass(); type != null && Exception.class.isAssignableFrom(type);
            type = type.getSuperclass()) {
            Handler handler = handlers.get(type);
            if (handler != null)
                return handler.invoke(failure);
        }
        throw invalid("No BotExceptionAdvice for " + failure.getClass().getName());
    }

    private static void register(Map<Class<? extends Exception>, Handler> registered, Object advice, Method method,
        BotExceptionHandler annotation, int order) {
        if (annotation.value().length == 0 || Modifier.isStatic(method.getModifiers())
            || method.getParameterCount() != 1 || !Exception.class.isAssignableFrom(method.getParameterTypes()[0])
            || !BotFailureDecision.class.isAssignableFrom(method.getReturnType())) {
            throw invalid("Invalid BotExceptionHandler signature: " + method);
        }
        // 从目标类读取注解，但实际调用代理上的可访问方法，不绕过 Advice 的 AOP 拦截。
        Method invocable = invocableMethod(method, advice.getClass());
        ReflectionUtils.makeAccessible(invocable);
        for (Class<? extends Exception> type : annotation.value()) {
            if (!method.getParameterTypes()[0].isAssignableFrom(type))
                throw invalid("BotExceptionHandler parameter cannot accept " + type.getName());
            Handler existing = registered.get(type);
            if (existing != null && existing.order() == order)
                throw invalid("Duplicate BotExceptionHandler for " + type.getName() + " at order " + order);
            if (existing == null || order < existing.order())
                registered.put(type, new Handler(advice, invocable, order));
        }
    }

    private static Method invocableMethod(Method method, Class<?> proxyType) {
        if (method.getDeclaringClass().isAssignableFrom(proxyType))
            return AopUtils.selectInvocableMethod(method, proxyType);
        for (Class<?> contract : proxyType.getInterfaces()) {
            try {
                Method candidate = contract.getMethod(method.getName(), method.getParameterTypes());
                if (BotFailureDecision.class.isAssignableFrom(candidate.getReturnType()))
                    return candidate;
            } catch (NoSuchMethodException ignored) {
                // Advice 方法未暴露在该接口上，继续检查其他代理接口。
            }
        }
        throw invalid("BotExceptionAdvice method is not exposed by its proxy: " + method);
    }

    private static SystemException invalid(String detail) {
        return new SystemException(CommandErrorCode.ADVICE_CONFIGURATION_INVALID, detail);
    }

    private record Handler(Object advice, Method method, int order) {
        BotFailureDecision invoke(Exception failure) {
            try {
                BotFailureDecision reply = (BotFailureDecision)method.invoke(advice, failure);
                if (reply == null)
                    throw invalid("BotExceptionHandler must return an explicit BotFailureDecision");
                return reply;
            } catch (InvocationTargetException invocationFailure) {
                Throwable cause = invocationFailure.getCause();
                if (cause instanceof Error fatal)
                    throw fatal;
                if (cause instanceof RuntimeException runtime)
                    throw runtime;
                throw new SystemException(CommandErrorCode.ADVICE_CONFIGURATION_INVALID,
                    "BotExceptionHandler failed: " + method, cause);
            } catch (IllegalAccessException accessFailure) {
                throw new SystemException(CommandErrorCode.ADVICE_CONFIGURATION_INVALID,
                    "Cannot invoke BotExceptionHandler: " + method, accessFailure);
            }
        }
    }
}
