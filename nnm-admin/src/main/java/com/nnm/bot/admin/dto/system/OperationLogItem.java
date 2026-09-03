package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

/** 管理端操作日志列表项。 */
public record OperationLogItem(
        /** 日志主键。 */
        Long id,
        /** 操作人主键。 */
        Long operatorId,
        /** 操作人名称。 */
        String operatorName,
        /** 操作动作。 */
        String action,
        /** 被操作对象类型。 */
        String targetType,
        /** 被操作对象标识。 */
        String targetId,
        /** 操作详情。 */
        String detail,
        /** 来源 IP。 */
        String ipAddress,
        /** 创建时间。 */
        LocalDateTime createdAt
) {
}
