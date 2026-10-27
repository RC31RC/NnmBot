package com.nnm.bot.core.onebot.connection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.mikuac.shiro.adapter.WebSocketClientHandler;
import com.mikuac.shiro.common.utils.PayloadSender;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import com.nnm.bot.core.onebot.message.OneBotMessageArrivalStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Shiro 客户端处理器的多实例版本。
 *
 * <p>Shiro 默认处理器在任一连接断开时会清空整个机器人容器，
 * 多个 WebSocket 地址同时使用时会误伤其他账号，因此这里只清理当前会话。</p>
 */
@Component
public class MultiOneBotWebSocketClientHandler extends WebSocketClientHandler {

    private final BotContainer botContainer;
    private final CoreEvent coreEvent;
    private final ThreadPoolTaskExecutor shiroTaskExecutor;
    private final ConcurrentMap<String, Set<Long>> sessionAccounts = new ConcurrentHashMap<>();
    @Resource
    private OneBotMessageArrivalStore messageArrivals;

    public MultiOneBotWebSocketClientHandler(
            EventHandler eventHandler,
            BotFactory botFactory,
            ActionHandler actionHandler,
            ShiroAsyncTask shiroAsyncTask,
            BotContainer botContainer,
            CoreEvent coreEvent,
            WebSocketProperties wsProperties,
            @Qualifier("shiroTaskExecutor")
            ThreadPoolTaskExecutor shiroTaskExecutor) {
        super(eventHandler, botFactory, actionHandler, shiroAsyncTask,
                botContainer, coreEvent, wsProperties, shiroTaskExecutor);
        this.botContainer = botContainer;
        this.coreEvent = coreEvent;
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    public void trackAccount(WebSocketSession session, long accountId) {
        sessionAccounts.computeIfAbsent(session.getId(), ignored -> ConcurrentHashMap.newKeySet())
                .add(accountId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        long receivedAt = System.currentTimeMillis();
        JsonObjectWrapper payload = JsonUtils.isValid(message.getPayload())
                ? JsonObjectWrapper.parseObject(message.getPayload()) : null;
        Object selfId = payload == null ? null : payload.get(Connection.SELF_ID);
        String accountId = selfId == null ? null : selfId.toString();
        if (payload != null && accountId != null && "message".equals(payload.get("post_type"))
                && payload.get("message_id") != null) {
            messageArrivals.record(session.getId(), accountId, payload.get("message_id").toString(), receivedAt);
        }
        if (ObjectUtil.isNotNull(accountId)) {
            session.getAttributes().put(Connection.X_SELF_ID, accountId);
            long numericAccountId = parseAccountId(accountId);
            if (numericAccountId != 0L) {
                trackAccount(session, numericAccountId);
                if (!botContainer.robots.containsKey(numericAccountId)) {
                    afterConnectionEstablished(session);
                }
            }
        }
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        PayloadSender.cleanupSessionLock(session.getId());
        Set<Long> accounts = sessionAccounts.remove(session.getId());
        if (CollUtil.isEmpty(accounts)) {
            long accountId = accountId(session);
            accounts = accountId == 0L ? Set.of() : Set.of(accountId);
        }
        for (long accountId : accounts) {
            removeSessionBot(session, accountId);
        }
    }
    // 移除当前会话的机器人
    private void removeSessionBot(WebSocketSession session, long accountId) {
        AtomicReference<Bot> removed = new AtomicReference<>();
        botContainer.robots.computeIfPresent(accountId, (key, bot) -> {
            if (ObjectUtil.isNotNull(bot.getSession()) && session.getId().equals(bot.getSession().getId())) {
                removed.set(bot);
                return null;
            }
            return bot;
        });
        if (ObjectUtil.isNull(removed.get())) {
            return;
        }
        CompletableFuture.runAsync(() -> coreEvent.offline(accountId), shiroTaskExecutor);
    }

    private static long parseAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            return 0L;
        }
        String normalized = accountId.trim();
        if (!normalized.chars().allMatch(Character::isDigit)) {
            return 0L;
        }
        String significant = normalized.replaceFirst("^0+(?!$)", "");
        if (significant.length() > 19
                || (significant.length() == 19
                && significant.compareTo("9223372036854775807") > 0)) {
            return 0L;
        }
        return Long.parseLong(normalized);
    }

    private static long accountId(WebSocketSession session) {
        Object value = session.getAttributes().get("x-self-id");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return ObjectUtil.isNull(value) ? 0L : parseAccountId(value.toString());
    }
}
