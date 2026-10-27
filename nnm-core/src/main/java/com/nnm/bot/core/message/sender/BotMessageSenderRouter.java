package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nnm.bot.core.message.model.BotMessagePart;

/**
 * 根据消息上下文中的平台选择协议发送器。
 *
 * <p>它只负责平台协议路由；展示选择和降级由 ReplyPlanner 在发送前完成。</p>
 */
@Component
public class BotMessageSenderRouter {

    @Resource
    private List<BotMessageSender> platformSenders;

    private Map<BotPlatform, BotMessageSender> senders = Map.of();

    @PostConstruct
    private void initialize() {
        // 数据库实例可在运行期新增，路由只绑定协议，账号由适配器在发送时解析。
        Map<BotPlatform, BotMessageSender> registry = new HashMap<>();
        for (BotMessageSender sender : this.platformSenders) {
            BotMessageSender previous = registry.put(sender.platform(), sender);
            if (ObjectUtil.isNotNull(previous)) {
                throw new BusinessException(
                        BotChannelErrorCode.MESSAGE_SENDER_NOT_REGISTERED,
                        "重复注册消息发送器: " + sender.platform());
            }
        }
        this.senders = Map.copyOf(registry);
    }

    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        return this.sender(context).supports(context, capability);
    }

    public boolean supportsComposition(BotMessageContext context, List<PartKind> parts) {
        return this.sender(context).supportsComposition(context, parts);
    }

    public boolean canReserve(BotMessageContext context, int count) {
        return this.sender(context).canReserve(context, count);
    }

    public List<BotMessageSendPermit> reserve(BotMessageContext context, int count) {
        return this.sender(context).reserve(context, count);
    }

    public PreparedBotMessage prepare(BotMessageContext context, List<BotMessagePart> blocks, String mentionUserId,
                                     BotMessageSendPermit permit) {
        return this.sender(context).prepare(context, blocks, mentionUserId, permit);
    }

    private BotMessageSender sender(BotMessageContext context) {
        Assert.notNull(context, "context must not be null");
        BotMessageSender sender = senders.get(context.platform());
        if (ObjectUtil.isNull(sender)) {
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SENDER_NOT_REGISTERED,
                    "未注册消息发送器: " + context.platform());
        }
        return sender;
    }
}
