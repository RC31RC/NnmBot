package com.nnm.bot.core.group.service;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.constant.GroupPointConstants.TransactionType;

/** 一次积分变更的业务参数，不暴露 JPA 实体。 */
public record PointOperation(long groupId, long memberId, String commandCode, TransactionType transactionType,
    long amount, String idempotencyKey, String description) {

    public PointOperation {
        BusinessAssert.isTrue(groupId > 0 && memberId > 0, CommonErrorCode.PARAM_INVALID, "积分账户标识必须为正数");
        commandCode = blankToNull(commandCode);
        BusinessAssert.notNull(transactionType, CommonErrorCode.PARAM_INVALID, "积分流水类型不能为空");
        BusinessAssert.isTrue(amount > 0 || (amount == 0 && transactionType == TransactionType.ADMIN_ADJUST),
            CommonErrorCode.PARAM_INVALID, "积分变更数量必须为正数，余额对齐允许为零");
        idempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        description = blankToNull(description);
    }

    private static String requireText(String value, String field) {
        return BusinessAssert.notBlank(value, CommonErrorCode.PARAM_INVALID, field + "不能为空").trim();
    }

    private static String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
