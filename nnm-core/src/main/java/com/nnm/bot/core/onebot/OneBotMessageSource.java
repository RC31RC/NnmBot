package com.nnm.bot.core.onebot;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

public record OneBotMessageSource(Bot bot, AnyMessageEvent event) {
}
