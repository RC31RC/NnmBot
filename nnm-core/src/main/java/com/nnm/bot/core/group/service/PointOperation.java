package com.nnm.bot.core.group.service;

import java.util.Objects;

/** 一次积分变更的业务参数，不暴露 JPA 实体。 */
public record PointOperation(long groupId,
                             long memberId,
                             String commandCode,
                             String transactionType,
                             long amount,
                             String idempotencyKey,
                             String description) {

    public PointOperation {
        if (groupId <= 0 || memberId <= 0) {
            throw new IllegalArgumentException("积分账户标识必须为正数");
        }
        commandCode = blankToNull(commandCode);
        transactionType = requireText(transactionType, "transactionType");
        if (amount <= 0) {
            throw new IllegalArgumentException("积分变更数量必须为正数");
        }
        idempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        description = blankToNull(description);
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(value, field + "不能为空").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + "不能为空");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
