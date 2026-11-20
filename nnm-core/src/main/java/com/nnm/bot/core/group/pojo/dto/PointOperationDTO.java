package com.nnm.bot.core.group.pojo.dto;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.core.group.constant.GroupPointConstants.PointErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.group.constant.GroupPointConstants.TransactionType;

/** 一次积分变更的业务参数，不暴露 JPA 实体。 */
public record PointOperationDTO(long groupId, long memberId, String commandCode, TransactionType transactionType,
    long amount, String idempotencyKey, String description) {

    public PointOperationDTO {
        BusinessAssert.isTrue(groupId > 0 && memberId > 0, PointErrorCode.ACCOUNT_ID_INVALID);
        commandCode = blankToNull(commandCode);
        BusinessAssert.notNull(transactionType, PointErrorCode.TRANSACTION_TYPE_REQUIRED);
        BusinessAssert.isTrue(amount > 0 || (amount == 0 && transactionType == TransactionType.ADMIN_ADJUST),
            PointErrorCode.AMOUNT_INVALID);
        idempotencyKey = requireText(idempotencyKey);
        description = blankToNull(description);
    }

    private static String requireText(String value) {
        return CharSequenceUtil.trim(BusinessAssert.notBlank(value, PointErrorCode.IDEMPOTENCY_KEY_REQUIRED));
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = CharSequenceUtil.trim(value);
        return CharSequenceUtil.isEmpty(normalized) ? null : normalized;
    }
}
