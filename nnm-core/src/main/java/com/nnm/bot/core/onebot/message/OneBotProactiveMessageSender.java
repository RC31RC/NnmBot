package com.nnm.bot.core.onebot.message;

import com.nnm.bot.core.message.model.BotMessagePart.Image;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.model.BotMessagePart.Text;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSender;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

/** 主动群消息直接调用群发送接口，保持图文顺序且不伪造入站事件。 */
@Component
public class OneBotProactiveMessageSender implements BotProactiveMessageSender {
    @Resource
    private BotContainer botContainer;
    @Resource
    private ReplyProperties limits;

    @Override
    public BotPlatform platform() { return BotPlatform.ONEBOT; }

    @Override
    public boolean supports(BotProactiveTarget target, BotChannelCapability capability) {
        return capability == BotChannelCapability.TEXT_MESSAGE || capability == BotChannelCapability.IMAGE_MESSAGE;
    }

    @Override
    public boolean supportsComposition(BotProactiveTarget target, List<PartKind> kinds) {
        return !kinds.isEmpty() && kinds.stream().allMatch(kind ->
                kind == PartKind.TEXT || kind == PartKind.IMAGE);
    }

    @Override
    public PreparedBotMessage prepare(BotProactiveTarget target, List<BotMessagePart> parts) {
        Bot bot = botContainer.robots.get(parseId(target.botAccountId()));
        if (bot == null) throw new BotMessageNotSentException("OneBot 机器人当前未连接");
        long group = parseId(target.conversationId());
        if (parts.isEmpty() || parts.stream().anyMatch(part -> !(part instanceof BotMessagePart.Text)
                && !(part instanceof BotMessagePart.Image))) {
            throw new BotMessageNotSentException("OneBot 主动消息不支持当前内容组合");
        }
        MsgUtils builder = MsgUtils.builder();
        target.mentionUserIds().forEach(user -> builder.at(parseId(user)));
        for (BotMessagePart part : parts) {
            switch (part) {
                case Text text -> builder.text(OneBotMessageSender.escapeText(text.content()));
                case Image image -> {
                    try {
                        builder.img(image.resource().readAllBytes(limits.getMaxImageBytes()));
                    } catch (IOException exception) {
                        throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                                "读取主动消息图片失败", exception);
                    }
                }
                default -> throw new BotMessageNotSentException("Unsupported proactive content");
            }
        }
        String message = builder.build();
        return new PreparedBotMessage(() -> OneBotMessageSender.receipt(bot.sendGroupMsg(group, message, false)),
                () -> { }, BotMessageSendPermit.unrestricted());
    }

    private long parseId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new BotMessageNotSentException("OneBot 账号、群组和提醒对象必须是有效数字", exception);
        }
    }
}
