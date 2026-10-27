package com.nnm.bot.admin.controller.assembler.bot.channel;

import com.nnm.bot.admin.pojo.dto.bot.channel.BotChannelInstanceDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotAccountProbeDTO;
import com.nnm.bot.admin.pojo.vo.bot.channel.BotChannelInstanceVO;
import com.nnm.bot.admin.pojo.vo.bot.channel.OneBotAccountProbeVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotChannelViewAssembler {
    private AdminBotChannelViewAssembler() {
    }

    public static BotChannelInstanceVO toBotChannelInstanceVO(BotChannelInstanceDTO value) {
        if (value == null) return null;
        return new BotChannelInstanceVO(
                value.id(),
                value.platform(),
                value.instanceId(),
                value.displayName(),
                value.botAccountId(),
                value.enabled(),
                value.connectionUrl(),
                value.accessTokenConfigured(),
                value.reconnectIntervalSeconds(),
                value.appId(),
                value.clientSecretConfigured(),
                value.tokenUrl(),
                value.apiBaseUrl(),
                value.gatewayUrl(),
                value.intents(),
                value.gatewayConnectTimeoutSeconds(),
                value.reconnectDelaySeconds(),
                value.runtimeStatus(),
                value.statusChangedAt(),
                value.lastError(),
                value.createdAt(),
                value.updatedAt());
    }

    public static List<BotChannelInstanceVO> toBotChannelInstanceVO(List<BotChannelInstanceDTO> values) {
        return values.stream().map(AdminBotChannelViewAssembler::toBotChannelInstanceVO).toList();
    }

    public static OneBotAccountProbeVO toOneBotAccountProbeVO(OneBotAccountProbeDTO value) {
        if (value == null) return null;
        return new OneBotAccountProbeVO(
                value.botAccountId());
    }

}
