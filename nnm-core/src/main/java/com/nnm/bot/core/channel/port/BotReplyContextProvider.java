package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.messaging.model.BotMessageContext;
import java.util.Optional;

/** 只有支持主动发送的适配器，才能创建不带原生回执的回复上下文。 */
public interface BotReplyContextProvider {

    /** 返回此上下文创建器所属的平台。 */
    BotPlatform platform();

    /** 在已授权的机器人绑定下创建主动消息上下文。
     *
     * @param instance 目标渠道实例元数据
     * @param binding 已配置的群组与机器人绑定
     * @param origin 触发当前流程的原始消息
     * @param senderId 目标平台用户或服务标识
     * @return 平台支持主动发送时返回上下文，否则为空
     */
    Optional<BotMessageContext> create(BotChannelInstanceMetadata instance, BotGroupBot binding,
        BotMessageContext origin, String senderId);
}
