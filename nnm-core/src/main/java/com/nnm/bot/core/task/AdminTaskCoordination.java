package com.nnm.bot.core.task;

import com.nnm.bot.core.constant.AdminTaskLockPurpose;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

/** 通过独立代理边界缩短任务预留时间，工作锁由实际执行线程持有。 */
@Service
public class AdminTaskCoordination {
    @Resource
    private DistributedLockManager lockManager;

    @RdtLock
    public <T> T reserve(String type, Supplier<T> reservation) {
        // 提交只锁住“查重 + 预留状态”；异步执行必须在自己的线程重新建立锁作用域。
        lockManager.lock(LockKey.forOperation(AdminTaskCoordination.class, AdminTaskLockPurpose.SUBMIT.key(), type));
        return reservation.get();
    }

    @RdtLock
    public void execute(String type, Callable<Void> work) throws Exception {
        // 同类型长任务串行，锁覆盖同步 work.call()；不能在回调里只提交后台工作就立即返回。
        lockManager.lock(LockKey.forOperation(AdminTaskCoordination.class, AdminTaskLockPurpose.WORK.key(), type));
        work.call();
    }
}
