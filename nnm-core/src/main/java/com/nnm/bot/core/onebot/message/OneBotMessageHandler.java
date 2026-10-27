package com.nnm.bot.core.onebot.message;

import com.nnm.bot.core.message.model.BotMessageContext;
import org.springframework.web.socket.WebSocketSession;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.plugin.execution.CommandIngressService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OneBot 入站消息适配器。
 *
 * <p>返回值仅表示命令链是否接管消息。已匹配但满载/停机拒绝仍返回 true，
 * 不让后续 SDK 处理器重复执行；异常不伪装成未匹配。</p>
 */
@Slf4j
@Shiro
@Component
public class OneBotMessageHandler {

    @Resource
    private CommandIngressService commandIngressService;
    @Resource
    private OneBotMessageArrivalStore messageArrivals;

    //    @GuildMessageHandler
//    @PrivateMessageHandler
//    @GroupMessageHandler
    @AnyMessageHandler
    public boolean onMessage(Bot bot, AnyMessageEvent event) {
        WebSocketSession session = bot.getSession();
        long receivedAt = messageArrivals.receivedAt(session.getId(), String.valueOf(bot.getSelfId()),
                String.valueOf(event.getMessageId()));
        BotMessageContext context = OneBotMessageContextFactory.from(bot, event).withReceivedAt(receivedAt);
        return commandIngressService.accept(context, session::isOpen).handled();
    }
}
