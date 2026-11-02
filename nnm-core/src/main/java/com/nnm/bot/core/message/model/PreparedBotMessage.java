package com.nnm.bot.core.message.model;

import com.nnm.bot.common.resource.ResourceScope;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;

/** 准备不能创建消息；创建最多尝试一次，清理和额度结算不改变已获得的回执。 */
public final class PreparedBotMessage implements AutoCloseable {
    /** 同一准备对象的发送和关闭必须互斥，防止附件在创建请求期间被删除。 */
    private final ReentrantLock sendLock = new ReentrantLock();
    /** 真正提交平台请求的动作，仅允许执行一次。 */
    private final Supplier<BotMessageReceipt> create;
    /** 当前准备对象拥有的本地资源与回复额度；清理先于关闭额度。 */
    private final ResourceScope resources = new ResourceScope();
    /** 平台回复额度，其接受状态独立于本地资源释放。 */
    private final BotMessageSendPermit permit;
    private boolean attempted;
    private boolean closed;

    /** 接管清理动作和额度，不在构造时提交平台请求。 */
    public PreparedBotMessage(Supplier<BotMessageReceipt> create, Runnable cleanup, BotMessageSendPermit permit) {
        this.create = Objects.requireNonNull(create, "消息创建动作不能为空");
        this.permit = Objects.requireNonNull(permit, "回复额度不能为空");
        Objects.requireNonNull(cleanup, "清理动作不能为空");
        resources.add(permit);
        resources.add(cleanup::run);
    }

    /** 串行执行一次创建；只有明确未提交或明确拒绝才归还回复额度。 */
    public BotMessageReceipt send() {
        return withStateLock(this::sendOnce);
    }

    /** 在本地状态锁内改变发送状态，未知回执保留已用额度。 */
    private BotMessageReceipt sendOnce() {
        if (closed || attempted)
            throw new IllegalStateException("消息已关闭或已经尝试发送");
        attempted = true;
        validatePermit();
        try {
            return Objects.requireNonNull(create.get(), "消息创建必须返回平台接受回执");
        } catch (BotMessageNotSentException | BotMessageRejectedException exception) {
            try {
                permit.rejected();
            } catch (RuntimeException settlementFailure) {
                exception.addSuppressed(settlementFailure);
            }
            throw exception;
        }
    }

    /** 发送动作尚未调用时的前置失败可以明确标记为未提交。 */
    private void validatePermit() {
        try {
            permit.beforeSend();
        } catch (RuntimeException exception) {
            throw new BotMessageNotSentException("消息发送前置条件不满足", exception);
        }
    }

    /** 幂等释放本地资源和额度；不会关闭 WebSocket，也不判断业务事务结果。 */
    @Override
    public void close() {
        withStateLock(() -> {
            if (!closed) {
                closed = true;
                resources.close();
            }
            return null;
        });
    }

    /** 将锁的机械释放集中在状态边界，不与消息创建逻辑交织。 */
    private <T> T withStateLock(Supplier<T> action) {
        sendLock.lock();
        try {
            return action.get();
        } finally {
            sendLock.unlock();
        }
    }
}
