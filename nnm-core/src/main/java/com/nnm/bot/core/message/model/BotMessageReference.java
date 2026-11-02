package com.nnm.bot.core.message.model;

import com.nnm.bot.core.constant.BotMessageConstants.ReferenceKind;
import java.util.List;
import java.util.Objects;

/**
 * 当前平台事件明确提供的一层引用，不包含可触发新命令的原文。
 *
 * @param kind 标识种类
 * @param id 原消息 ID 或渠道引用索引
 * @param attachments 平台同时提供的原消息附件；无内容时由渠道服务按需读取
 */
public record BotMessageReference(ReferenceKind kind, String id, List<BotIncomingAttachment> attachments) {
    public BotMessageReference {
        Objects.requireNonNull(kind, "引用类型不能为空");
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("引用标识不能为空");
        attachments = List.copyOf(attachments);
    }
}
