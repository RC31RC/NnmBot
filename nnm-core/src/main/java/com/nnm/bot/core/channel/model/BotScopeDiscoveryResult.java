package com.nnm.bot.core.channel.model;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

/**
 * 渠道侧作用域发现结果。结果只用于管理端展示，不直接创建或修改持久化作用域。
 *
 * @param supported 当前渠道是否支持发现
 * @param message 发现结果说明或失败原因
 * @param scopes 发现到的作用域列表
 */
public record BotScopeDiscoveryResult(
        boolean supported,
        String message,
        List<BotDiscoveredScope> scopes) {

    public BotScopeDiscoveryResult {
        scopes = CollUtil.isEmpty(scopes) ? List.of() : List.copyOf(scopes);
    }

    public static BotScopeDiscoveryResult unsupported(String message) {
        return new BotScopeDiscoveryResult(false, message, List.of());
    }
}
