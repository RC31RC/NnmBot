package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

/** 跨实例串行化短暂的“查询冲突、删除旧预览、登记新任务”阶段，不串行化不同文件的下载。 */
@Service
public class WarframeOfficialRefreshCoordination {
    @Resource
    private DistributedLockManager locks;

    /**
     * 对所有官方刷新入口使用同一个登记锁，业务执行仍可按文件并行。
     *
     * @param work 需要原子执行的检查与登记动作。
     * @return 登记动作的结果。
     */
    @RdtLock
    public <T> T reserve(Supplier<T> work) {
        locks.lock(LockKey.named(WarframeOfficialRefreshCoordination.class,
            OfficialDataConstants.LockPurpose.ADMISSION.key()));
        return work.get();
    }
}
