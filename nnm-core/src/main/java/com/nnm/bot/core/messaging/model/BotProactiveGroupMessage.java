package com.nnm.bot.core.messaging.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;

import java.util.List;

/**
 * 脱离入站消息上下文的主动群消息。
 *
 * @param botAccountId 平台侧机器人账号
 * @param conversationId 平台侧群组标识
 * @param text 消息正文
 * @param mentionUserIds 需要提醒的平台用户标识；不支持提醒的渠道会忽略
 */
public record BotProactiveGroupMessage(String botAccountId, String conversationId, String text,
    List<String> mentionUserIds) {

    public BotProactiveGroupMessage {
        botAccountId = CharSequenceUtil.trim(botAccountId);
        conversationId = CharSequenceUtil.trim(conversationId);
        text = CharSequenceUtil.trim(text);
        mentionUserIds = CollUtil.isEmpty(mentionUserIds) ? List.of() : mentionUserIds.stream()
            .map(CharSequenceUtil::trim).filter(CharSequenceUtil::isNotBlank).distinct().toList();
    }
}
