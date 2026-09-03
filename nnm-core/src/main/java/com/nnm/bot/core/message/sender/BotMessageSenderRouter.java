package com.nnm.bot.core.message.sender;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据消息上下文中的平台选择协议发送器。
 *
 * <p>它只做平台路由，不决定文本、图片、文件之间的降级策略；
 * 输出组合和降级由各平台 {@code BotChannelResponseStrategy} 负责。</p>
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

    public void sendText(BotMessageContext context, String text) {
        this.sender(context).sendText(context, text);
    }

    public void sendText(BotMessageContext context, String text, String mentionUserId) {
        this.sender(context).sendText(context, text, mentionUserId);
    }

    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        return this.sender(context).supports(context, capability);
    }

    public void sendImage(BotMessageContext context, byte[] image) {
        this.sender(context).sendImage(context, image);
    }

    public void sendImage(BotMessageContext context, byte[] image, String mentionUserId) {
        this.sender(context).sendImage(context, image, mentionUserId);
    }

    public void sendImageWithText(BotMessageContext context, String text, byte[] image) {
        this.sender(context).sendImageWithText(context, text, image);
    }

    public void sendImageWithText(BotMessageContext context, String text, byte[] image,
                                  String mentionUserId) {
        this.sender(context).sendImageWithText(context, text, image, mentionUserId);
    }

    public void sendFile(BotMessageContext context, BotFile file) {
        this.sender(context).sendFile(context, file);
    }

    public void sendFile(BotMessageContext context, BotFile file, String mentionUserId) {
        this.sender(context).sendFile(context, file, mentionUserId);
    }

    public void sendFileWithText(BotMessageContext context, String text, BotFile file) {
        this.sender(context).sendFileWithText(context, text, file);
    }

    public void sendFileWithText(BotMessageContext context, String text, BotFile file,
                                 String mentionUserId) {
        this.sender(context).sendFileWithText(context, text, file, mentionUserId);
    }

    public void sendMarkdown(BotMessageContext context, BotMarkdownMessage message) {
        this.sender(context).sendMarkdown(context, message);
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
