package com.nnm.bot.core.task;

import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

/** Separate proxy boundaries keep reservation short and the worker lock on its executing thread. */
@Service
public class AdminTaskCoordination {
    @Resource
    private DistributedLockManager lockManager;

    @RdtLock
    public <T> T reserve(String type, Supplier<T> reservation) {
        // 提交只锁住“查重 + 预留状态”；异步执行必须在自己的线程重新建立锁作用域。
        lockManager.lock("admin-task-submit:" + type);
        return reservation.get();
    }

    @RdtLock
    public void execute(String type, Callable<Void> work) throws Exception {
        // 同类型长任务串行，锁覆盖同步 work.call()；不能在回调里只提交后台工作就立即返回。
        lockManager.lock("admin-task-work:" + type);
        work.call();
    }
}
