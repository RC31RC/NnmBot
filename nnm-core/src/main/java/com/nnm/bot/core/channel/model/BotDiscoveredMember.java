package com.nnm.bot.core.channel.model;

/**
 * 渠道发现到的成员，只保存平台侧身份，不自动等同于系统用户。
 *
 * @param platformUserId 平台侧成员标识
 * @param displayName 平台侧显示名称
 * @param role 平台侧成员角色
 */
public record BotDiscoveredMember(
        String platformUserId,
        String displayName,
        String role) {
}
