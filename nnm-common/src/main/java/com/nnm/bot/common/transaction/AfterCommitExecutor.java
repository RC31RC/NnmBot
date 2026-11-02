package com.nnm.bot.common.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在当前事务成功提交后同步执行动作。 没有实际事务时立即执行；本组件 不切换线程，也不吞掉动作抛出的异常。
 */
@Component
public class AfterCommitExecutor {
    private static final Object BATCH_RESOURCE_KEY = new Object();

    public void execute(Runnable action) {
        Runnable requiredAction = Objects.requireNonNull(action, "action");
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            requiredAction.run();
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                "An active transaction requires transaction synchronization for after-commit work");
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                requiredAction.run();
            }
        });
    }

    /**
     * 按事务内的键登记一次动作；同键重复调用 共用已登记回调，不同键保持各自的异常 与执行边界。
     */
    public void executeOnce(Object key, Runnable action) {
        Object requiredKey = Objects.requireNonNull(key, "key");
        Runnable requiredAction = Objects.requireNonNull(action, "action");
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            requiredAction.run();
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                "An active transaction requires transaction synchronization for after-commit work");
        }

        @SuppressWarnings("unchecked")
        Map<Object, Runnable> actions =
            (Map<Object, Runnable>)TransactionSynchronizationManager.getResource(BATCH_RESOURCE_KEY);
        if (actions != null) {
            actions.putIfAbsent(requiredKey, requiredAction);
            return;
        }

        Map<Object, Runnable> registered = new LinkedHashMap<>();
        registered.put(requiredKey, requiredAction);
        TransactionSynchronizationManager.bindResource(BATCH_RESOURCE_KEY, registered);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                runAll(registered);
            }

            @Override
            public void afterCompletion(int status) {
                if (TransactionSynchronizationManager.hasResource(BATCH_RESOURCE_KEY)) {
                    TransactionSynchronizationManager.unbindResource(BATCH_RESOURCE_KEY);
                }
            }
        });
    }

    private void runAll(Map<Object, Runnable> actions) {
        Throwable failure = null;
        for (Runnable action : actions.values()) {
            try {
                action.run();
            } catch (RuntimeException | Error exception) {
                if (failure == null)
                    failure = exception;
                else
                    failure.addSuppressed(exception);
            }
        }
        if (failure instanceof RuntimeException exception)
            throw exception;
        if (failure instanceof Error error)
            throw error;
    }
}
