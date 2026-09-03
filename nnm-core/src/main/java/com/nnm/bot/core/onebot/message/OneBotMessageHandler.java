package com.nnm.bot.core.onebot.message;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.plugin.execution.CommandDispatcher;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * OneBot 入站消息适配器。
 *
 * <p>Shiro 在 WebSocket 线程调用该入口；这里不承载业务逻辑，只把协议事件
 * 转换为平台无关上下文并交给命令分发器。</p>
 */
@Shiro
@Component
public class OneBotMessageHandler {

    @Resource
    private CommandDispatcher commandDispatcher;

    @AnyMessageHandler
    public boolean onMessage(Bot bot, AnyMessageEvent event) {
        return this.commandDispatcher.dispatch(OneBotMessageContextFactory.from(bot, event));
    }
}
