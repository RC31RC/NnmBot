package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.OperationLogDTO;
import com.nnm.bot.common.response.PageResult;

/** 记录管理操作并提供分页审计查询。 */
public interface OperationAuditService {

    /**
     * 以当前操作人记录审计事件。
     *
     * @param action 操作编码
     * @param targetType 目标资源类型
     * @param targetId 目标资源标识
     * @param detail 经脱敏的操作摘要
     */
    void record(String action, String targetType, Object targetId, String detail);

    /**
     * 为指定操作人记录审计事件。
     *
     * @param operatorId 操作人主键
     * @param action 操作编码
     * @param targetType 目标资源类型
     * @param targetId 目标资源标识
     * @param detail 经脱敏的操作摘要
     */
    void recordForOperator(long operatorId, String action, String targetType, Object targetId, String detail);

    /**
     * 按操作人名称筛选审计记录。
     *
     * @param operatorName 操作人名称，空值表示不过滤
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @return 分页审计记录
     */
    PageResult<OperationLogDTO> logs(String operatorName, int current, int size);
}
