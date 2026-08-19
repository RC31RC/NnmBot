package com.nnm.bot.core.message;

/**
 * 可选的 Markdown 消息能力。
 *
 * <p>不是所有消息平台都支持 Markdown，因此该能力不并入基础发送接口。</p>
 */
public interface MarkdownMessageSender {

    void sendMarkdown(BotMessageContext context, BotMarkdownMessage message);
}
