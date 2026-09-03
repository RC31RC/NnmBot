package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;

public interface BotMessageSender {

    BotPlatform platform();

    void sendText(BotMessageContext context, String text);

    /** 发送文字并可选提及触发者；默认渠道保持原有协议。 */
    default void sendText(BotMessageContext context, String text, String mentionUserId) {
        sendText(context, text);
    }

    default boolean supports(BotMessageContext context, BotChannelCapability capability) {
        return capability == BotChannelCapability.TEXT_MESSAGE;
    }

    /**
     * 渠道未实现媒体能力时抛出该异常，由响应策略使用命令文本降级。
     */
    default void sendImage(BotMessageContext context, byte[] image) {
        throw new BotChannelCapabilityException(
                this.platform(), BotChannelCapability.IMAGE_MESSAGE, "图片消息");
    }

    /**
     * 发送带说明文字的图片。渠道必须显式实现自己的组合协议；
     * 未实现时交给响应策略统一降级为文本。
     */
    default void sendImageWithText(BotMessageContext context, String text, byte[] image) {
        throw new BotChannelCapabilityException(
                this.platform(), BotChannelCapability.IMAGE_MESSAGE, "图文组合消息");
    }

    /** 发送图片并可选提及触发者。 */
    default void sendImage(BotMessageContext context, byte[] image, String mentionUserId) {
        sendImage(context, image);
    }

    /** 发送图文并可选提及触发者。 */
    default void sendImageWithText(BotMessageContext context, String text, byte[] image,
                                   String mentionUserId) {
        sendImageWithText(context, text, image);
    }

    default void sendFile(BotMessageContext context, BotFile file) {
        throw new BotChannelCapabilityException(
                this.platform(), BotChannelCapability.FILE_MESSAGE, "文件消息");
    }

    default void sendFileWithText(BotMessageContext context, String text, BotFile file) {
        throw new BotChannelCapabilityException(
                this.platform(), BotChannelCapability.FILE_MESSAGE, "文件与文字组合消息");
    }

    /** 发送文件并可选提及触发者。 */
    default void sendFile(BotMessageContext context, BotFile file, String mentionUserId) {
        sendFile(context, file);
    }

    /** 发送文件和文字并可选提及触发者。 */
    default void sendFileWithText(BotMessageContext context, String text, BotFile file,
                                  String mentionUserId) {
        sendFileWithText(context, text, file);
    }

    default void sendMarkdown(BotMessageContext context, BotMarkdownMessage message) {
        throw new BotChannelCapabilityException(
                this.platform(), BotChannelCapability.MARKDOWN_MESSAGE, "Markdown 消息");
    }
}
