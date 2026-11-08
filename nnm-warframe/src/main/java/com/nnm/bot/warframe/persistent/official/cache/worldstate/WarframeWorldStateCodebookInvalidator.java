package com.nnm.bot.warframe.persistent.official.cache.worldstate;

import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.mq.message.persistent.official.worldstate.WarframeWorldStateCodebookInvalidationMessage;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在事务提交后刷新本实例的世界状态引用资料，并通知其他实例失效。 消费者只执行本地刷新，避免循环广播。
 */
@Component
@Slf4j
public class WarframeWorldStateCodebookInvalidator {

    @Resource
    private WarframeWorldStateCodebookService codebook;
    @Resource
    private RdtMessageSendManager messages;
    @Resource
    private AfterCommitExecutor afterCommitExecutor;

    /**
     * 在当前事务成功提交后刷新相关缓存。
     */
    public void invalidateAfterCommit() {
        // 提交后缓存失效失败不能把已提交的文件事务误报为失败；定时刷新和 MQ 消费可重试。
        afterCommitExecutor.execute(() -> {
            try {
                invalidateNow();
            } catch (RuntimeException exception) {
                log.warn("官方数据已提交，世界状态代码簿刷新失败；等待定时重试: {}", exception.toString());
            }
        });
    }

    /**
     * 立即使本实例相关业务缓存失效。
     */
    public void invalidateNow() {
        reloadAndBroadcast();
    }

    /**
     * 仅刷新本实例资料缓存。
     */
    public void reloadLocal() {
        codebook.reload();
    }

    /**
     * 刷新本实例资料缓存并通知其他实例失效。
     */
    private void reloadAndBroadcast() {
        RuntimeException failure = null;
        try {
            codebook.reload();
        } catch (RuntimeException exception) {
            failure = exception;
        }
        try {
            messages.send(new WarframeWorldStateCodebookInvalidationMessage("invalidate"));
        } catch (RuntimeException exception) {
            if (failure == null) {
                failure = exception;
            } else {
                failure.addSuppressed(exception);
            }
        }
        if (failure != null) {
            log.error("WorldStateDTO 代码簿数据库写入已提交，但本地刷新或跨实例广播失败", failure);
            throw failure;
        }
    }
}
