package com.nnm.bot.core.onebot.connection;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

/**
 * 对底层 WebSocket 的发送入口再做一次同步保护。
 *
 * <p>Shiro 的 action 锁按 session id 工作，但连接层可能同时存在多个
 * session 包装对象。这里保证所有经过当前 Bot 的发送最终串行进入底层 session。</p>
 */
final class SerializedWebSocketSession extends WebSocketSessionDecorator {

    private final ReentrantLock sendLock = new ReentrantLock(true);
    private final BooleanSupplier ownership;

    SerializedWebSocketSession(WebSocketSession delegate) {
        this(delegate, () -> true);
    }

    SerializedWebSocketSession(WebSocketSession delegate, BooleanSupplier ownership) {
        super(delegate);
        this.ownership = ownership;
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        this.sendLock.lock();
        try {
            if (!ownership.getAsBoolean()) {
                close();
                throw new IOException("Bot connection ownership lost");
            }
            if (!isOpen()) {
                return;
            }
            try {
                super.sendMessage(message);
            } catch (IllegalStateException exception) {
                // 连接可能在 isOpen() 检查和实际发送之间被 OneBot 关闭。
                // 该消息已经没有可投递的连接，不应反向污染命令处理线程。
                if (isOpen()) {
                    throw exception;
                }
            }
        } finally {
            this.sendLock.unlock();
        }
    }
}
