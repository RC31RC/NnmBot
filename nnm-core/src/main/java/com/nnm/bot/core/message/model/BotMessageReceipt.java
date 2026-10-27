package com.nnm.bot.core.message.model;

/** 平台明确接受的创建回执；文件上传等协议没有消息 ID 时保持 null，不伪造 ID。 */
public record BotMessageReceipt(String platformMessageId) {
}
