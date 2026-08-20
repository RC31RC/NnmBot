package com.nnm.bot.core.onebot;

import com.mikuac.shiro.common.utils.PayloadSender;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Shiro 客户端处理器的多实例版本。
 *
 * <p>Shiro 默认处理器在任一连接断开时会清空整个机器人容器，
 * 多个 WebSocket 地址同时使用时会误伤其他账号，因此这里只清理当前会话。</p>
 */
@Component
public class MultiOneBotWebSocketClientHandler extends com.mikuac.shiro.adapter.WebSocketClientHandler {

    private final BotContainer botContainer;
    private final CoreEvent coreEvent;
    private final ThreadPoolTaskExecutor shiroTaskExecutor;

    public MultiOneBotWebSocketClientHandler(
            EventHandler eventHandler,
            BotFactory botFactory,
            ActionHandler actionHandler,
            ShiroAsyncTask shiroAsyncTask,
            BotContainer botContainer,
            CoreEvent coreEvent,
            WebSocketProperties wsProperties,
            @Qualifier("shiroTaskExecutor") ThreadPoolTaskExecutor shiroTaskExecutor) {
        super(eventHandler, botFactory, actionHandler, shiroAsyncTask,
                botContainer, coreEvent, wsProperties, shiroTaskExecutor);
        this.botContainer = botContainer;
        this.coreEvent = coreEvent;
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        PayloadSender.cleanupSessionLock(session.getId());
        long accountId = accountId(session);
        AtomicReference<Bot> removed = new AtomicReference<>();
        if (accountId != 0L) {
            botContainer.robots.computeIfPresent(accountId, (key, bot) -> {
                if (bot.getSession().getId().equals(session.getId())) {
                    removed.set(bot);
                    return null;
                }
                return bot;
            });
        }
        if (removed.get() != null) {
            shiroTaskExecutor.execute(() -> coreEvent.offline(accountId));
        }
    }

    private static long accountId(WebSocketSession session) {
        Object value = session.getAttributes().get("x-self-id");
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return value == null ? 0L : Long.parseLong(value.toString());
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }
}
