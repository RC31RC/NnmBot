package com.nnm.bot.admin.pojo.query.system;

/** 操作日志列表查询参数。 */
public record OperationLogQuery(
        String operatorName,
        Integer current,
        Integer size
) {
}
