package com.nnm.bot.admin.dto.system;

/** 操作日志列表查询参数。 */
public record OperationLogQueryRequest(
        String operatorName,
        Integer current,
        Integer size
) {
}
