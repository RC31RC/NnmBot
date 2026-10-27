package com.nnm.bot.admin.pojo.dto.bot.scope;

import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;

import java.util.List;

/** 单个机器人实例的作用域发现结果。 */
public record BotScopeDiscoveryDTO(
        /** 机器人实例主键。 */
        long botInstanceId,
        /** 实例编码。 */
        String instanceId,
        /** 实例展示名称。 */
        String displayName,
        /** 接入平台。 */
        BotPlatform platform,
        /** 当前运行状态。 */
        BotChannelStatus runtimeStatus,
        /** 是否支持主动发现。 */
        boolean supported,
        /** 发现说明或失败原因。 */
        String message,
        /** 发现到的作用域列表。 */
        List<BotScopeDiscoveryScopeDTO> scopes) {
}
