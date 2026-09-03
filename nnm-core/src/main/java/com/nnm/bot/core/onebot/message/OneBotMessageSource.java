package com.nnm.bot.core.onebot.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

/**
 * OneBot 消息来源，保留协议 SDK 对象供渠道适配层使用。
 *
 * @param bot 当前 OneBot 机器人客户端
 * @param event OneBot 原始消息事件
 */
public record OneBotMessageSource(Bot bot, AnyMessageEvent event) {
}
