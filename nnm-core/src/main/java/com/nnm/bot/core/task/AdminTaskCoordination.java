package com.nnm.bot.core.task;

import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Separate proxy boundaries keep reservation short and the worker lock on its executing thread. */
@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AdminTaskCoordination {
    @Resource
    private DistributedLockManager lockManager;

    @RdtLock
    public <T> T reserve(String type, Supplier<T> reservation) {
        lockManager.lock("admin-task-submit:" + type);
        return reservation.get();
    }

    @RdtLock
    public void execute(String type, Callable<Void> work) throws Exception {
        lockManager.lock("admin-task-work:" + type);
        work.call();
    }
}
