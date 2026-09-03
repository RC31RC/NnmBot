package com.nnm.bot.core.group.service;

import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;

/**
 * 已通过渠道实例和平台作用域解析出的逻辑群组上下文。
 * 命令权限、成员身份和积分边界都必须基于这个上下文继续处理。
 */
public record BotGroupCommandContext(BotGroup group, BotGroupBot binding) {
}
