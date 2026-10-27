package com.nnm.bot.core.message.model;

import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;

/** 准备不能创建消息；创建最多尝试一次，清理和额度结算不改变已获得的回执。 */
public final class PreparedBotMessage implements AutoCloseable {
    private final ReentrantLock sendLock = new ReentrantLock();

    private final Supplier<BotMessageReceipt> create;
    private final Runnable cleanup;
    private final BotMessageSendPermit permit;
    private boolean attempted;
    private boolean closed;

    public PreparedBotMessage(Supplier<BotMessageReceipt> create, Runnable cleanup, BotMessageSendPermit permit) {
        this.create = Objects.requireNonNull(create, "create");
        this.cleanup = Objects.requireNonNull(cleanup, "cleanup");
        this.permit = Objects.requireNonNull(permit, "permit");
    }

    public BotMessageReceipt send() {
        sendLock.lock();
        try {
            if (closed || attempted) throw new IllegalStateException("Prepared message is closed or already attempted");
            attempted = true;
            try {
                permit.beforeSend();
            } catch (RuntimeException exception) {
                throw new BotMessageNotSentException("Message send precondition failed", exception);
            }
            try {
                return Objects.requireNonNull(create.get(), "Message creation must return an acceptance receipt");
            } catch (BotMessageNotSentException | BotMessageRejectedException exception) {
                try {
                    permit.rejected();
                } catch (RuntimeException settlementFailure) {
                    exception.addSuppressed(settlementFailure);
                }
                throw exception;
            }
            // 超时、断线、Error 和缺失回执都保留已使用额度；不能在 finally 中退款。
        } finally {
            sendLock.unlock();
        }
    }

    @Override
    public void close() {
        sendLock.lock();
        try {
            if (closed) return;
            closed = true;
            Throwable failure = null;
            try {
                cleanup.run();
            } catch (RuntimeException | Error exception) {
                failure = exception;
            }
            try {
                permit.close();
            } catch (RuntimeException | Error settlementFailure) {
                if (failure == null) failure = settlementFailure;
                else failure.addSuppressed(settlementFailure);
            }
            if (failure instanceof RuntimeException exception) throw exception;
            if (failure instanceof Error error) throw error;
        } finally {
            sendLock.unlock();
        }
    }
}
