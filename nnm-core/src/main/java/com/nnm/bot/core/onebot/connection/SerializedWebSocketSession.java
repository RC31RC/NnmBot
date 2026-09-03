package com.nnm.bot.core.onebot.connection;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对底层 WebSocket 的发送入口再做一次同步保护。
 *
 * <p>Shiro 的 action 锁按 session id 工作，但连接层可能同时存在多个
 * session 包装对象。这里保证所有经过当前 Bot 的发送最终串行进入底层 session。</p>
 */
final class SerializedWebSocketSession extends WebSocketSessionDecorator {

    private final ReentrantLock sendLock = new ReentrantLock(true);

    SerializedWebSocketSession(WebSocketSession delegate) {
        super(delegate);
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        this.sendLock.lock();
        try {
            super.sendMessage(message);
        } finally {
            this.sendLock.unlock();
        }
    }
}
