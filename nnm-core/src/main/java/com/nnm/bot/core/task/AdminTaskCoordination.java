package com.nnm.bot.core.task;

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

    /** taskType 是业务互斥分组，如世界状态刷新或某个版本下的数据集刷新，不是任务实例 ID。 */
    @RdtLock
    public <T> T reserve(String taskType, Supplier<T> reservation) {
        // 提交只锁住“查重 + 预留状态”；异步执行必须在自己的线程重新建立锁作用域。
        lockManager.lock(LockKey.forResource(AdminTaskReservationLock.class, taskType));
        return reservation.get();
    }

    @RdtLock
    public void execute(String taskType, Callable<Void> work) throws Exception {
        // 同类型长任务串行，工作期间的进度更新可短暂获取独立的预留锁。
        // 锁覆盖同步 work.call()；不能在回调里只提交后台工作就立即返回。
        lockManager.lock(LockKey.forResource(AdminTaskExecutionLock.class, taskType));
        work.call();
    }

    // 当前只在执行期间短暂获取预留锁；新增反向嵌套时须重新评估跨类型死锁风险。
    // RDT 以类的简单名作为资源类型，改名会改变跨实例共享锁键，必须让所有实例同步切换。
    static final class AdminTaskReservationLock {
        private AdminTaskReservationLock() {}
    }

    static final class AdminTaskExecutionLock {
        private AdminTaskExecutionLock() {}
    }
}
