package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

public record OperationLogItem(
        Long id,
        Long operatorId,
        String operatorName,
        String action,
        String targetType,
        String targetId,
        String detail,
        String ipAddress,
        LocalDateTime createdAt
) {
}
