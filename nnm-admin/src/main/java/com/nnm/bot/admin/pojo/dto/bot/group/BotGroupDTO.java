package com.nnm.bot.admin.pojo.dto.bot.group;

import com.nnm.bot.core.constant.BotChannelStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/** 逻辑群组及其机器人绑定展示对象。 */
public record BotGroupDTO(
        /** 群组主键。 */
        Long id,
        /** 群组编码。 */
        String code,
        /** 群组名称。 */
        String name,
        /** 群组说明。 */
        String description,
        /** 是否启用。 */
        boolean enabled,
        /** 群组成员数量。 */
        long memberCount,
        /** 绑定的机器人列表。 */
        List<BotBindingDTO> bots,
        /** 创建时间。 */
        LocalDateTime createdAt,
        /** 最后更新时间。 */
        LocalDateTime updatedAt) {

    /** 逻辑群组中的机器人绑定。 */
    public record BotBindingDTO(
            /** 绑定记录主键。 */
            Long id,
            /** 机器人实例主键。 */
            Long botInstanceId,
            /** 机器人实例编码。 */
            String instanceId,
            /** 机器人实例名称。 */
            String displayName,
            /** 平台编码。 */
            String platform,
            /** 作用域类型。 */
            String scopeType,
            /** 平台侧作用域标识。 */
            String scopeKey,
            /** 平台侧作用域名称。 */
            String scopeDisplayName,
            /** 是否启用绑定。 */
            boolean enabled,
            /** 回复优先级，数值越小越优先。 */
            int replyPriority,
            /** 机器人实例当前运行状态。 */
            BotChannelStatus runtimeStatus,
            /** 运行状态最后变更时间。 */
            Instant runtimeStatusChangedAt,
            /** 最近一次运行错误。 */
            String lastError) {
    }
}
