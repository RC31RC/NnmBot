package com.nnm.bot.common.validation;

import com.nnm.bot.common.error.ErrorCode;
import com.nnm.bot.common.exception.BusinessException;

import java.util.Collection;
import java.util.Objects;

/**
 * 业务前置条件校验工具，统一业务异常的构造方式。
 */
public final class BusinessAssert {

    private BusinessAssert() {
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        isTrue(expression, errorCode, errorCode.getMessage());
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode) {
        isFalse(expression, errorCode, errorCode.getMessage());
    }

    public static void isFalse(boolean expression, ErrorCode errorCode, String message) {
        if (expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static <T> T notNull(T value, ErrorCode errorCode) {
        return notNull(value, errorCode, errorCode.getMessage());
    }

    public static <T> T notNull(T value, ErrorCode errorCode, String message) {
        if (value == null) {
            throw new BusinessException(errorCode, message);
        }
        return value;
    }

    public static String notBlank(String value, ErrorCode errorCode) {
        return notBlank(value, errorCode, errorCode.getMessage());
    }

    public static String notBlank(String value, ErrorCode errorCode, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(errorCode, message);
        }
        return value;
    }

    public static <T extends Collection<?>> T notEmpty(T value, ErrorCode errorCode) {
        return notEmpty(value, errorCode, errorCode.getMessage());
    }

    public static <T extends Collection<?>> T notEmpty(
            T value, ErrorCode errorCode, String message) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
        return value;
    }

    public static void equals(Object actual, Object expected, ErrorCode errorCode, String message) {
        isTrue(Objects.equals(actual, expected), errorCode, message);
    }

    public static void notEquals(
            Object actual, Object expected, ErrorCode errorCode, String message) {
        isFalse(Objects.equals(actual, expected), errorCode, message);
    }
}
